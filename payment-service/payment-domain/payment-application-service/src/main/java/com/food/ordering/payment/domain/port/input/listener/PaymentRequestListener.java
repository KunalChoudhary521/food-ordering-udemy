package com.food.ordering.payment.domain.port.input.listener;

import com.food.ordering.payment.domain.dto.PaymentRequest;

public interface PaymentRequestListener {

    void completePayment(PaymentRequest paymentRequest);
    void cancelPayment(PaymentRequest paymentRequest);
}
