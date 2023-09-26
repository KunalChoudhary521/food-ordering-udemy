package com.food.ordering.order.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCancelledEvent> orderCancelledDomainEventPublisher;

    public OrderCancelledEvent(Order order, ZonedDateTime createdAt,
                               DomainEventPublisher<OrderCancelledEvent> orderCancelledDomainEventPublisher) {
        super(order, createdAt);
        this.orderCancelledDomainEventPublisher = orderCancelledDomainEventPublisher;
    }

    @Override
    public void publish() {
        orderCancelledDomainEventPublisher.publish(this);
    }
}
