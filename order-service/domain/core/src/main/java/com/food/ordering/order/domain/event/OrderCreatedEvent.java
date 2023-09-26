package com.food.ordering.order.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCreatedEvent> orderCreatedDomainEventPublisher;

    public OrderCreatedEvent(Order order, ZonedDateTime createdAt,
                             DomainEventPublisher<OrderCreatedEvent> orderCreatedDomainEventPublisher) {
        super(order, createdAt);
        this.orderCreatedDomainEventPublisher = orderCreatedDomainEventPublisher;
    }

    @Override
    public void publish() {
        orderCreatedDomainEventPublisher.publish(this);
    }
}
