package com.food.ordering.payment.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.kafka.producer.KafkaMessagePublisher;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.payment.domain.config.PaymentServiceConfig;
import com.food.ordering.payment.domain.outbox.model.OrderEventPayload;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.payment.domain.port.output.publisher.PaymentResponsePublisher;
import com.food.ordering.payment.messaging.mapper.PaymentMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentEventKafkaPublisher implements PaymentResponsePublisher {

    private final PaymentMessagingMapper paymentMessagingMapper;
    private final KafkaProducer<String, PaymentResponse> kafkaProducer;
    private final PaymentServiceConfig paymentServiceConfig;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload = kafkaMessagePublisher.getOrderEventPayload(orderOutboxMessage.getPayload(),
                OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}", orderEventPayload.getOrderId(), sagaId);

        try {
            PaymentResponse paymentResponse = paymentMessagingMapper.orderEventPayloadToPaymentResponseAvroModel(sagaId,
                    orderEventPayload);

            kafkaProducer.send(paymentServiceConfig.getPaymentResponseTopic(), sagaId, paymentResponse,
                    kafkaMessagePublisher.getKafkaCallback(paymentServiceConfig.getPaymentResponseTopic(),
                            paymentResponse,
                            orderOutboxMessage,
                            outboxCallback,
                            paymentResponse.getOrderId(),
                            paymentResponse.getClass().getSimpleName()));
        } catch (Exception e) {
            log.error("Error while sending PaymentResponse to kafka for order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }
}
