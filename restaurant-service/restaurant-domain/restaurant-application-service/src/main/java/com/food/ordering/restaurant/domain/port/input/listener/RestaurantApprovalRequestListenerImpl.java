package com.food.ordering.restaurant.domain.port.input.listener;

import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.domain.RestaurantDomainService;
import com.food.ordering.restaurant.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderApprovalEvent;
import com.food.ordering.restaurant.domain.exception.RestaurantDomainException;
import com.food.ordering.restaurant.domain.mapper.RestaurantMapper;
import com.food.ordering.restaurant.domain.outbox.model.OrderEventPayload;
import com.food.ordering.restaurant.domain.outbox.scheduler.OrderOutboxHelper;
import com.food.ordering.restaurant.domain.port.output.publisher.RestaurantApprovalResponsePublisher;
import com.food.ordering.restaurant.domain.port.output.repository.OrderApprovalRepository;
import com.food.ordering.restaurant.domain.port.output.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class RestaurantApprovalRequestListenerImpl implements RestaurantApprovalRequestListener {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderOutboxHelper orderOutboxHelper;
    private final RestaurantApprovalResponsePublisher restaurantApprovalResponsePublisher;

    @Override
    @Transactional
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        if(isOutboxMessagePublished(restaurantApprovalRequest)) {
            log.info("Restaurant - outbox message with saga id: {} is already persisted in DB", restaurantApprovalRequest.getSagaId());
            return;
        }

        log.info("Received restaurant approval request for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();

        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages);
        orderApprovalRepository.save(restaurant.getOrderApproval());

        OrderEventPayload orderEventPayload = restaurantMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent);
        orderOutboxHelper.saveOrderOutboxMessage(orderEventPayload, orderApprovalEvent.getOrderApproval().getApprovalStatus(),
                OutboxStatus.STARTED, UUID.fromString(restaurantApprovalRequest.getSagaId()));
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        return restaurantRepository.findRestaurant(restaurant)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Restaurant with id: %s not found", restaurant.getId().getValue().toString());
                    log.error(errorMessage);
                    throw new RestaurantDomainException(errorMessage);
                });
    }

    private boolean isOutboxMessagePublished(RestaurantApprovalRequest restaurantApprovalRequest) {
        return orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
                        UUID.fromString(restaurantApprovalRequest.getSagaId()), OutboxStatus.COMPLETED)
                .map(message -> {
                    restaurantApprovalResponsePublisher.publish(message, orderOutboxHelper::updateOutboxStatus);
                    return true;
                })
                .orElse(false);
    }
}
