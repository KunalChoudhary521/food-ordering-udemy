package com.food.ordering.order.domain.port.output.publisher;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.order.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestPublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
