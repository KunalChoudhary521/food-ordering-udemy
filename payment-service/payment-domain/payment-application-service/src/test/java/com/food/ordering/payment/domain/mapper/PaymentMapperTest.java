package com.food.ordering.payment.domain.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.PaymentStatus;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.event.PaymentFailedEvent;
import com.food.ordering.payment.domain.outbox.model.OrderEventPayload;
import com.food.ordering.payment.domain.valueobject.PaymentId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentMapperTest {

    private static final PaymentId TEST_PAYMENT_ID = new PaymentId(UUID.fromString("b9cf08d0-70e5-4f4d-a923-bb43df9b9256"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("149d067c-113b-4e47-9981-fc6a8473d660"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("42f46504-cef2-4985-b694-66def178e4ed"));
    private static final Money TEST_PRICE = new Money(new BigDecimal("17.49"));
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 10, 6, 10, 0, 0, 0, ZoneOffset.UTC);

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

    @Test
    void paymentEvent_paymentEventToOrderEventPayload_orderEventPayload() {
        Payment payment = Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .customerId(TEST_CUSTOMER_ID)
                .orderId(TEST_ORDER_ID)
                .price(TEST_PRICE)
                .paymentStatus(PaymentStatus.CANCELLED)
                .build();
        PaymentFailedEvent paymentEvent = new PaymentFailedEvent(payment, TEST_ZONE_DATE_TIME, List.of("fail1", "fail2"));

        OrderEventPayload orderEventPayload = paymentMapper.paymentEventToOrderEventPayload(paymentEvent);

        assertEquals(paymentEvent.getPayment().getId().getValue().toString(), orderEventPayload.getPaymentId());
        assertEquals(paymentEvent.getPayment().getCustomerId().getValue().toString(), orderEventPayload.getCustomerId());
        assertEquals(paymentEvent.getPayment().getOrderId().getValue().toString(), orderEventPayload.getOrderId());
        assertEquals(paymentEvent.getPayment().getPrice().getAmount(), orderEventPayload.getPrice());
        assertEquals(paymentEvent.getCreatedAt(), orderEventPayload.getCreatedAt());
        assertEquals(paymentEvent.getPayment().getPaymentStatus().toString(), orderEventPayload.getPaymentStatus());
        assertThat(orderEventPayload.getFailureMessages()).contains(paymentEvent.getFailureMessages().toArray(String[]::new));
    }
}