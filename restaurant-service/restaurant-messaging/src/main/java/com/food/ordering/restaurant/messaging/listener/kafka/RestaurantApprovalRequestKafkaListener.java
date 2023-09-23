package com.food.ordering.restaurant.messaging.listener.kafka;

import com.food.ordering.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.payment.domain.port.input.listener.RestaurantApprovalRequestListener;
import com.food.ordering.restaurant.messaging.mapper.RestaurantMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<com.food.ordering.kafka.order.model.RestaurantApprovalRequest> {

    private final RestaurantApprovalRequestListener restaurantApprovalRequestListener;
    private final RestaurantMessagingMapper restaurantMessagingMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${restaurant-service.restaurant-approval-request-topic}")
    public void receive(@Payload List<RestaurantApprovalRequest> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} restaurant approval requests received with keys: {}, partitions: {}, offsets: {}", messages.size(),
                keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(restaurantApprovalRequestAvroModel -> {
            log.info("Restaurant approval request paid for order with id: {}", restaurantApprovalRequestAvroModel.getOrderId());

            com.food.ordering.payment.domain.dto.RestaurantApprovalRequest restaurantApprovalRequest =
                    restaurantMessagingMapper.restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(restaurantApprovalRequestAvroModel);
            restaurantApprovalRequestListener.approveOrder(restaurantApprovalRequest);
        });
    }
}
