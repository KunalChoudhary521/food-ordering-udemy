package com.food.ordering.payment.domain.port.output.publisher;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.event.PaymentFailedEvent;

public interface PaymentFailedPublisher extends DomainEventPublisher<PaymentFailedEvent> {
}
