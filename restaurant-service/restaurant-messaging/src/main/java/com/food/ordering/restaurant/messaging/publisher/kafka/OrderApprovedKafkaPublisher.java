package com.food.ordering.restaurant.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.RestaurantApprovalResponse;
import com.food.ordering.kafka.producer.KafkaMessagePublisher;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.payment.domain.config.RestaurantServiceConfig;
import com.food.ordering.payment.domain.port.output.publisher.OrderApprovedPublisher;
import com.food.ordering.restaurant.domain.event.OrderApprovedEvent;
import com.food.ordering.restaurant.messaging.mapper.RestaurantMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class OrderApprovedKafkaPublisher implements OrderApprovedPublisher {

    private final RestaurantMessagingMapper restaurantMessagingMapper;
    private final RestaurantServiceConfig restaurantServiceConfig;
    private final KafkaProducer<String, RestaurantApprovalResponse> kafkaProducer;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Override
    public void publish(OrderApprovedEvent domainEvent) {
        String orderId = domainEvent.getOrderApproval().getOrderId().getValue().toString();
        log.info("Received OrderApprovedEvent with order id: {}", orderId);

        try {
            RestaurantApprovalResponse restaurantApprovalResponse = restaurantMessagingMapper.orderApprovedEventToRestaurantApprovalResponseAvroModel(domainEvent);

            String topic = restaurantServiceConfig.getRestaurantApprovalResponseTopic();
            kafkaProducer.send(topic, orderId, restaurantApprovalResponse,
                    kafkaMessagePublisher.getKafkaCallback(topic, restaurantApprovalResponse, orderId,
                            restaurantApprovalResponse.getClass().getSimpleName()));
        } catch (Exception ex) {
            log.error("Failed to send RestaurantApprovalResponse message with order id {}, error {}", orderId, ex);
        }
    }
}
