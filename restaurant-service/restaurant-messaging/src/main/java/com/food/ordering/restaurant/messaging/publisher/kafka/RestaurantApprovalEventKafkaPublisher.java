package com.food.ordering.restaurant.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.RestaurantApprovalResponse;
import com.food.ordering.kafka.producer.KafkaMessagePublisher;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.domain.config.RestaurantServiceConfig;
import com.food.ordering.restaurant.domain.outbox.model.OrderEventPayload;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.restaurant.domain.port.output.publisher.RestaurantApprovalResponsePublisher;
import com.food.ordering.restaurant.messaging.mapper.RestaurantMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
@Slf4j
public class RestaurantApprovalEventKafkaPublisher implements RestaurantApprovalResponsePublisher {

    private final RestaurantMessagingMapper restaurantMessagingMapper;
    private final KafkaProducer<String, RestaurantApprovalResponse> kafkaProducer;
    private final RestaurantServiceConfig restaurantServiceConfig;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload = kafkaMessagePublisher.getOrderEventPayload(orderOutboxMessage.getPayload(),
                OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Restaurant - Received OrderOutboxMessage for order id: {} and saga id: {}", orderEventPayload.getOrderId(), sagaId);

        try {
            RestaurantApprovalResponse restaurantApprovalResponse =
                    restaurantMessagingMapper.orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, orderEventPayload);

            kafkaProducer.send(restaurantServiceConfig.getRestaurantApprovalResponseTopic(), sagaId, restaurantApprovalResponse,
                    kafkaMessagePublisher.getKafkaCallback(restaurantServiceConfig.getRestaurantApprovalResponseTopic(),
                            restaurantApprovalResponse,
                            orderOutboxMessage,
                            outboxCallback,
                            restaurantApprovalResponse.getOrderId(),
                            restaurantApprovalResponse.getClass().getSimpleName()));
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponse to kafka for order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }

    }
}
