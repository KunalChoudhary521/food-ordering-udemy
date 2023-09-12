package com.food.ordering.payment.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedDomainEventPublisher;

    public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt,
                                 DomainEventPublisher<PaymentCompletedEvent> paymentCompletedDomainEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCompletedDomainEventPublisher = paymentCompletedDomainEventPublisher;
    }

    @Override
    public void publish() {
        paymentCompletedDomainEventPublisher.publish(this);
    }
}
