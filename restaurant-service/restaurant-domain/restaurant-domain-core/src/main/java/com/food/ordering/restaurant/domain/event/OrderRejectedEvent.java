package com.food.ordering.restaurant.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderRejectedEvent> orderRejectedDomainEventPublisher;

    public OrderRejectedEvent(OrderApproval orderApproval, RestaurantId restaurantId,
                              List<String> failureMessages, ZonedDateTime createdAt,
                              DomainEventPublisher<OrderRejectedEvent> orderRejectedDomainEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.orderRejectedDomainEventPublisher = orderRejectedDomainEventPublisher;
    }

    @Override
    public void publish() {
        orderRejectedDomainEventPublisher.publish(this);
    }
}
