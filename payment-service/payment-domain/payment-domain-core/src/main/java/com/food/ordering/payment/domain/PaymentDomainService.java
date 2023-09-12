package com.food.ordering.payment.domain;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.payment.domain.entity.CreditEntry;
import com.food.ordering.payment.domain.entity.CreditHistory;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.event.PaymentCancelledEvent;
import com.food.ordering.payment.domain.event.PaymentCompletedEvent;
import com.food.ordering.payment.domain.event.PaymentEvent;
import com.food.ordering.payment.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories, List<String> failureMessages,
                                            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedDomainEventPublisher,
                                            DomainEventPublisher<PaymentFailedEvent> paymentFailedDomainEventPublisher);

    PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories, List<String> failureMessages,
                                          DomainEventPublisher<PaymentCancelledEvent> paymentCancelledDomainEventPublisher,
                                          DomainEventPublisher<PaymentFailedEvent> paymentFailedDomainEventPublisher);
}
