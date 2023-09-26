package com.food.ordering.payment.domain.event;

import com.food.ordering.domain.event.DomainEvent;
import com.food.ordering.payment.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class PaymentEvent implements DomainEvent<Payment> {

    private final Payment payment;
    private final ZonedDateTime createdAt;
    private final List<String> failureMessages;
}
