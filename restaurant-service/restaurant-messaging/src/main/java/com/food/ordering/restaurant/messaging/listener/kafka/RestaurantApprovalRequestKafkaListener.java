package com.food.ordering.restaurant.messaging.listener.kafka;

import com.food.ordering.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.restaurant.domain.exception.RestaurantApplicationServiceException;
import com.food.ordering.restaurant.domain.exception.RestaurantNotFoundException;
import com.food.ordering.restaurant.domain.port.input.listener.RestaurantApprovalRequestListener;
import com.food.ordering.restaurant.messaging.mapper.RestaurantMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
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
            try {
                log.info("Restaurant approval request paid for order with id: {}", restaurantApprovalRequestAvroModel.getOrderId());

                com.food.ordering.restaurant.domain.dto.RestaurantApprovalRequest restaurantApprovalRequest =
                        restaurantMessagingMapper.restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(restaurantApprovalRequestAvroModel);
                restaurantApprovalRequestListener.approveOrder(restaurantApprovalRequest);
            } catch (DataAccessException e) {
                SQLException sqlException = (SQLException) e.getRootCause();
                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    log.error("Restaurant - Unique constraint exception with sql state: {} in RestaurantApprovalRequestKafkaListener"
                            + " for order id: {}", sqlException.getSQLState(), restaurantApprovalRequestAvroModel.getOrderId());
                } else {
                    throw new RestaurantApplicationServiceException("Throwing DataAccessException in RestaurantApprovalRequestKafkaListener: "
                            + e.getMessage(), e);
                }
            } catch (RestaurantNotFoundException e) {
                log.error("No restaurant found for restaurant id: {}, and order id: {}",
                        restaurantApprovalRequestAvroModel.getRestaurantId(),
                        restaurantApprovalRequestAvroModel.getOrderId());
            }
        });
    }
}
