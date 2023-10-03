package com.food.ordering.order.messaging.publisher.kafka;

import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.kafka.producer.KafkaMessagePublisher;
import com.food.ordering.kafka.producer.service.KafkaProducer;
import com.food.ordering.order.domain.config.OrderServiceConfig;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.order.domain.port.output.publisher.RestaurantApprovalRequestPublisher;
import com.food.ordering.order.messaging.mapper.OrderMessagingMapper;
import com.food.ordering.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
@Slf4j
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestPublisher {

    private final OrderMessagingMapper orderMessagingMapper;
    private final KafkaProducer<String, RestaurantApprovalRequest> kafkaProducer;
    private final OrderServiceConfig orderServiceConfig;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    @Override
    public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                        BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback) {
        OrderApprovalEventPayload orderApprovalEventPayload =
                kafkaMessagePublisher.getOrderEventPayload(orderApprovalOutboxMessage.getPayload(), OrderApprovalEventPayload.class);

        String sagaId = orderApprovalOutboxMessage.getSagaId().toString();

        log.info("Received OrderApprovalOutboxMessage for order id: {} and saga id: {}", orderApprovalEventPayload.getOrderId(), sagaId);

        try {
            RestaurantApprovalRequest restaurantApprovalRequest =
                    orderMessagingMapper.orderApprovalEventPayloadToRestaurantApprovalRequestAvroModel(sagaId, orderApprovalEventPayload);

            kafkaProducer.send(orderServiceConfig.getRestaurantApprovalRequestTopic(), sagaId, restaurantApprovalRequest,
                    kafkaMessagePublisher.getKafkaCallback(orderServiceConfig.getRestaurantApprovalRequestTopic(),
                            restaurantApprovalRequest,
                            orderApprovalOutboxMessage,
                            outboxCallback,
                            restaurantApprovalRequest.getOrderId(),
                            restaurantApprovalRequest.getClass().getSimpleName()));

        } catch (Exception e) {
            log.error("Error while sending OrderApprovalEventPayload to kafka for order id: {} and saga id: {}, error: {}",
                    orderApprovalEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }
}
