package com.food.ordering.order.domain.port.input.listener;

import com.food.ordering.order.domain.dto.message.PaymentResponse;

public interface PaymentResponseListener {

    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentCancelled(PaymentResponse paymentResponse);
}
