package com.food.ordering.payment.domain.port.output.publisher;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.restaurant.domain.event.OrderApprovedEvent;

public interface OrderApprovedPublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
