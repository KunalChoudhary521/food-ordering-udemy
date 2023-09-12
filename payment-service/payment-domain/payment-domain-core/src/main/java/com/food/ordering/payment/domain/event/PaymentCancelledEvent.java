package com.food.ordering.payment.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCancelledEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCancelledEvent> paymentCancelledDomainEventPublisher;

    public PaymentCancelledEvent(Payment payment, ZonedDateTime createdAt,
                                 DomainEventPublisher<PaymentCancelledEvent> paymentCancelledDomainEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCancelledDomainEventPublisher = paymentCancelledDomainEventPublisher;
    }

    @Override
    public void publish() {
        paymentCancelledDomainEventPublisher.publish(this);
    }
}
