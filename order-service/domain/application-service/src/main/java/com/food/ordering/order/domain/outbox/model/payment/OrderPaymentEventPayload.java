package com.food.ordering.order.domain.outbox.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderPaymentEventPayload {

    private String orderId;
    private String customerId;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private String paymentOrderStatus;
}
