package com.food.ordering.payment.data.access.payment.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.PaymentStatus;
import com.food.ordering.payment.data.access.payment.entity.PaymentEntity;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.valueobject.PaymentId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentDataMapperTest {

    private static final PaymentId TEST_PAYMENT_ID = new PaymentId(UUID.fromString("24f4a020-d49e-4680-a423-df2cacb2f0f7"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("a725ead9-ffdf-42f8-a0a4-3833294fab58"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("20232769-ef18-4506-bb26-a61b1e551bf2"));
    private static final Money TEST_PRICE = new Money(new BigDecimal("20.23"));
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 9, 12, 22, 15, 21, 0, ZoneOffset.UTC);

    private final PaymentDataMapper paymentDataMapper = Mappers.getMapper(PaymentDataMapper.class);

    @Test
    void payment_paymentToPaymentEntity_paymentEntity() {
        Payment payment = Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .orderId(TEST_ORDER_ID)
                .customerId(TEST_CUSTOMER_ID)
                .price(TEST_PRICE)
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdAt(TEST_ZONE_DATE_TIME)
                .build();

        PaymentEntity paymentEntity = paymentDataMapper.paymentToPaymentEntity(payment);

        assertEquals(payment.getId().getValue(), paymentEntity.getId());
        assertEquals(payment.getCustomerId().getValue(), paymentEntity.getCustomerId());
        assertEquals(payment.getOrderId().getValue(), paymentEntity.getOrderId());
        assertEquals(payment.getPrice().getAmount(), paymentEntity.getPrice());
        assertEquals(payment.getPaymentStatus(), paymentEntity.getStatus());
        assertEquals(payment.getCreatedAt(), paymentEntity.getCreatedAt());
    }

    @Test
    void paymentEntity_paymentEntityToPayment_payment() {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(TEST_PAYMENT_ID.getValue())
                .orderId(TEST_ORDER_ID.getValue())
                .customerId(TEST_CUSTOMER_ID.getValue())
                .price(TEST_PRICE.getAmount())
                .status(PaymentStatus.FAILED)
                .createdAt(TEST_ZONE_DATE_TIME)
                .build();

        Payment payment = paymentDataMapper.paymentEntityToPayment(paymentEntity);

        assertEquals(paymentEntity.getId(), payment.getId().getValue());
        assertEquals(paymentEntity.getCustomerId(), payment.getCustomerId().getValue());
        assertEquals(paymentEntity.getOrderId(), payment.getOrderId().getValue());
        assertEquals(paymentEntity.getPrice(), payment.getPrice().getAmount());
        assertEquals(paymentEntity.getStatus(), payment.getPaymentStatus());
        assertEquals(paymentEntity.getCreatedAt(), payment.getCreatedAt());
    }
}