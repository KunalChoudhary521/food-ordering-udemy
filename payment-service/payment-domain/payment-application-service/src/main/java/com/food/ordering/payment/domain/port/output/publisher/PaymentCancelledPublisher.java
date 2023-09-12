package com.food.ordering.payment.domain.port.output.publisher;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledPublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
