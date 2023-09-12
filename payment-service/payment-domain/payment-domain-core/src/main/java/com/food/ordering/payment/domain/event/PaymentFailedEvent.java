package com.food.ordering.payment.domain.event;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentFailedEvent> paymentFailedDomainEventPublisher;

    public PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages,
                              DomainEventPublisher<PaymentFailedEvent> paymentFailedDomainEventPublisher) {
        super(payment, createdAt, failureMessages);
        this.paymentFailedDomainEventPublisher = paymentFailedDomainEventPublisher;
    }

    @Override
    public void publish() {
        paymentFailedDomainEventPublisher.publish(this);
    }
}
