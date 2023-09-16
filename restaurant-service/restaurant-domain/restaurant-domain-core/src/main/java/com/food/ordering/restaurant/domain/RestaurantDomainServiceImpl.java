package com.food.ordering.restaurant.domain;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderApprovalEvent;
import com.food.ordering.restaurant.domain.event.OrderApprovedEvent;
import com.food.ordering.restaurant.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    private final static ZonedDateTime CURRENT_UTC_TIME = ZonedDateTime.now(ZoneId.of("UTC"));

    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages,
                                            DomainEventPublisher<OrderApprovedEvent> orderApprovedDomainEventPublisher,
                                            DomainEventPublisher<OrderRejectedEvent> orderRejectedDomainEventPublisher) {
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(restaurant.getOrderApproval(),  restaurant.getId(), failureMessages,
                    CURRENT_UTC_TIME, orderApprovedDomainEventPublisher);
        } else {
            log.info("Order is rejected for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(restaurant.getOrderApproval(), restaurant.getId(), failureMessages,
                    CURRENT_UTC_TIME, orderRejectedDomainEventPublisher);
        }
    }
}
