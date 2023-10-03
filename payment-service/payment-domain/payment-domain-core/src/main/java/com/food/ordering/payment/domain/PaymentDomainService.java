package com.food.ordering.payment.domain;

import com.food.ordering.payment.domain.entity.CreditEntry;
import com.food.ordering.payment.domain.entity.CreditHistory;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment, CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories, List<String> failureMessages);

    PaymentEvent validateAndCancelPayment(Payment payment, CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories, List<String> failureMessages);
}
