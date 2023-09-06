package com.food.ordering.order.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.order.domain.config.OrderServiceConfig;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.port.output.publisher.OrderCancelledPaymentRequestPublisher;
import com.food.ordering.order.messaging.mapper.OrderMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CancelOrderKafkaPublisher implements OrderCancelledPaymentRequestPublisher {

    private final OrderMessagingMapper orderMessagingMapper;
    private final OrderServiceConfig orderServiceConfig;
    private final KafkaProducer<String, PaymentRequest> kafkaProducer;
    private final OrderKafkaMessagePublisher orderKafkaMessagePublisher;

    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCancelledEvent for order id: {}", orderId);

         try {
            PaymentRequest paymentRequest = orderMessagingMapper.orderCancelledEventToPaymentRequest(domainEvent);

             String topic = orderServiceConfig.getPaymentRequestTopic();
             kafkaProducer.send(topic, orderId, paymentRequest,
                    orderKafkaMessagePublisher.getKafkaCallback(topic, paymentRequest, orderId,
                            paymentRequest.getClass().getSimpleName()));
        } catch (Exception ex) {
             log.error("Failed to send PaymentRequest message with order id {}, error {}", orderId, ex);
         }
    }
}
