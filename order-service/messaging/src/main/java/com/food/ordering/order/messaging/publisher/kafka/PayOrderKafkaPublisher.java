package com.food.ordering.order.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.order.domain.config.OrderServiceConfig;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.order.domain.port.output.publisher.OrderPaidRestaurantRequestPublisher;
import com.food.ordering.order.messaging.mapper.OrderMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PayOrderKafkaPublisher implements OrderPaidRestaurantRequestPublisher {

    private final OrderMessagingMapper orderMessagingMapper;
    private final OrderServiceConfig orderServiceConfig;
    private final KafkaProducer<String, RestaurantApprovalRequest> kafkaProducer;
    private final OrderKafkaMessagePublisher orderKafkaMessagePublisher;

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCancelledEvent for order id: {}", orderId);

        try {
            RestaurantApprovalRequest restaurantApprovalRequest = orderMessagingMapper.orderPaidEventToRestaurantApprovalRequest(domainEvent);

            String topic = orderServiceConfig.getRestaurantApprovalRequestTopic();
            kafkaProducer.send(topic, orderId, restaurantApprovalRequest,
                    orderKafkaMessagePublisher.getKafkaCallback(topic, restaurantApprovalRequest, orderId,
                            restaurantApprovalRequest.getClass().getSimpleName()));
        } catch (Exception ex) {
            log.error("Failed to send RestaurantApprovalRequest message with order id {}, error {}", orderId, ex);
        }
    }
}
