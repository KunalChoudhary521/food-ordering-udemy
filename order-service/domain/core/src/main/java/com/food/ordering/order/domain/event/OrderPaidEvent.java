package com.food.ordering.order.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    private final DomainEventPublisher<OrderPaidEvent> orderPaidDomainEventPublisher;

    public OrderPaidEvent(Order order, ZonedDateTime createdAt,
                          DomainEventPublisher<OrderPaidEvent> orderPaidDomainEventPublisher) {
        super(order, createdAt);
        this.orderPaidDomainEventPublisher = orderPaidDomainEventPublisher;
    }

    @Override
    public void publish() {
        orderPaidDomainEventPublisher.publish(this);
    }
}
