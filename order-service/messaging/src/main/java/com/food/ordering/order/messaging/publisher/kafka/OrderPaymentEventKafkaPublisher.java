package com.food.ordering.order.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.kafka.producer.KafkaMessagePublisher;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.order.domain.config.OrderServiceConfig;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.order.domain.port.output.publisher.PaymentRequestPublisher;
import com.food.ordering.order.messaging.mapper.OrderMessagingMapper;
import com.food.ordering.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
@Slf4j
public class OrderPaymentEventKafkaPublisher implements PaymentRequestPublisher {

    private final OrderMessagingMapper orderMessagingMapper;
    private final KafkaProducer<String, PaymentRequest> kafkaProducer;
    private final OrderServiceConfig orderServiceConfig;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Override
    public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                        BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {
        OrderPaymentEventPayload orderPaymentEventPayload =
                kafkaMessagePublisher.getOrderEventPayload(orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);

        String sagaId = orderPaymentOutboxMessage.getSagaId().toString();

        log.info("Received OrderPaymentOutboxMessage for order id: {} and saga id: {}", orderPaymentEventPayload.getOrderId(), sagaId);

        try {
            PaymentRequest paymentRequest = orderMessagingMapper.orderPaymentEventPayloadToPaymentRequestAvroModel(sagaId, orderPaymentEventPayload);

            kafkaProducer.send(orderServiceConfig.getPaymentRequestTopic(), sagaId, paymentRequest,
                    kafkaMessagePublisher.getKafkaCallback(orderServiceConfig.getPaymentRequestTopic(),
                            paymentRequest,
                            orderPaymentOutboxMessage,
                            outboxCallback,
                            orderPaymentEventPayload.getOrderId(),
                            paymentRequest.getClass().getSimpleName()));
        } catch (Exception e) {
            log.error("Error while sending OrderPaymentEventPayload to kafka with order id: {} and saga id: {}, error: {}",
                    orderPaymentEventPayload.getOrderId(), sagaId, e.getMessage());
        }

    }
}
