package com.food.ordering.order.messaging.listener.kafka;

import com.food.ordering.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.kafka.order.model.OrderApprovalStatus;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.port.input.listener.RestaurantApprovalResponseListener;
import com.food.ordering.order.messaging.mapper.OrderMessagingMapper;
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
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<com.food.ordering.kafka.order.model.RestaurantApprovalResponse> {

    private final static String FAILURE_MESSAGE_DELIMITER = ",";

    private final OrderMessagingMapper orderMessagingMapper;
    private final RestaurantApprovalResponseListener restaurantApprovalResponseListener;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic}")
    public void receive(@Payload List<com.food.ordering.kafka.order.model.RestaurantApprovalResponse> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} restaurant approval responses received with keys: {}, partitions: {}, offsets: {}", messages.size(),
                keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            if(OrderApprovalStatus.APPROVED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                log.info("Restaurant approved order with id: {}", restaurantApprovalResponseAvroModel.getOrderId());
                RestaurantApprovalResponse restaurantApprovalResponse =
                        orderMessagingMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(restaurantApprovalResponseAvroModel);
                restaurantApprovalResponseListener.orderApproved(restaurantApprovalResponse);
            } else if(OrderApprovalStatus.REJECTED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                log.info("Restaurant rejected order with id: {}, with failure messages: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(),
                        String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponseAvroModel.getFailureMessages()));
                RestaurantApprovalResponse restaurantApprovalResponse =
                        orderMessagingMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(restaurantApprovalResponseAvroModel);
                restaurantApprovalResponseListener.orderRejected(restaurantApprovalResponse);
            }
        });
    }
}
