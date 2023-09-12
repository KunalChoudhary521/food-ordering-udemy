package com.food.ordering.payment.domain.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.entity.Payment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentMapperTest {

    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("149d067c-113b-4e47-9981-fc6a8473d660"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("42f46504-cef2-4985-b694-66def178e4ed"));
    public static final Money TEST_PRICE = new Money(new BigDecimal("17.49"));

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void paymentRequest_paymentRequestModelToPayment_payment() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(TEST_ORDER_ID.getValue().toString())
                .customerId(TEST_CUSTOMER_ID.getValue().toString())
                .price(TEST_PRICE.getAmount())
                .build();

        Payment payment = paymentMapper.paymentRequestModelToPayment(paymentRequest);

        assertEquals(paymentRequest.getCustomerId(), payment.getCustomerId().getValue().toString());
        assertEquals(paymentRequest.getOrderId(), payment.getOrderId().getValue().toString());
        assertEquals(paymentRequest.getPrice(), payment.getPrice().getAmount());
    }
}