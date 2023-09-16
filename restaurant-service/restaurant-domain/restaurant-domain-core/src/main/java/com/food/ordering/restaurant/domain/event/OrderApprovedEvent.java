package com.food.ordering.restaurant.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderApprovedEvent> orderApprovedDomainEventPublisher;

    public OrderApprovedEvent(OrderApproval orderApproval, RestaurantId restaurantId,
                              List<String> failureMessages, ZonedDateTime createdAt,
                              DomainEventPublisher<OrderApprovedEvent> orderApprovedDomainEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.orderApprovedDomainEventPublisher = orderApprovedDomainEventPublisher;
    }

    @Override
    public void publish() {
        orderApprovedDomainEventPublisher.publish(this);
    }
}
