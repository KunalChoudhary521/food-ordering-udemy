package com.food.ordering.payment.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.kafka.producer.KafkaMessagePublisher;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.payment.domain.config.PaymentServiceConfig;
import com.food.ordering.payment.domain.event.PaymentFailedEvent;
import com.food.ordering.payment.domain.port.output.publisher.PaymentFailedPublisher;
import com.food.ordering.payment.messaging.mapper.PaymentMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentFailedKafkaPublisher implements PaymentFailedPublisher {

    private final PaymentMessagingMapper paymentMessagingMapper;
    private final PaymentServiceConfig paymentServiceConfig;
    private final KafkaProducer<String, PaymentResponse> kafkaProducer;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Override
    public void publish(PaymentFailedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();
        log.info("Received PaymentFailedEvent with order id: {}", orderId);

        try {
            PaymentResponse paymentResponse = paymentMessagingMapper.paymentEventToPaymentResponseAvroModel(domainEvent);

            String topic = paymentServiceConfig.getPaymentRequestTopic();
            kafkaProducer.send(topic, orderId, paymentResponse,
                    kafkaMessagePublisher.getKafkaCallback(topic, paymentResponse, orderId,
                            paymentResponse.getClass().getSimpleName()));
        } catch (Exception ex) {
            log.error("Failed to send PaymentResponse message with order id {}, error {}", orderId, ex);
        }

    }
}
