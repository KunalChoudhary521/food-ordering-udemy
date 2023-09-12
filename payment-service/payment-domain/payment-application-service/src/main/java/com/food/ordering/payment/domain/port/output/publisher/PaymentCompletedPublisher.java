package com.food.ordering.payment.domain.port.output.publisher;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.event.PaymentCompletedEvent;

public interface PaymentCompletedPublisher extends DomainEventPublisher<PaymentCompletedEvent> {
}
