package com.food.ordering.order.messaging.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.PaymentStatus;
import com.food.ordering.kafka.order.model.PaymentOrderStatus;
import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.event.PaymentCancelledEvent;
import com.food.ordering.payment.domain.event.PaymentCompletedEvent;
import com.food.ordering.payment.domain.event.PaymentFailedEvent;
import com.food.ordering.payment.domain.valueobject.PaymentId;
import com.food.ordering.payment.messaging.mapper.PaymentMessagingMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentMessagingMapperTest {

    private static final UUID TEST_PAYMENT_REQUEST_EVENT_ID = UUID.fromString("af71693e-df85-43b0-84bf-5de058a8d1e8");
    private static final PaymentId TEST_PAYMENT_ID = new PaymentId(UUID.fromString("22f1ec47-d440-4d1c-8ee4-9b312317a3a1"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("7ff24312-78c6-4dcf-8bb6-7a2cf07f0137"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("ac9ff3b1-5d70-4202-97a2-cb25030f9f3a"));
    public static final Money TEST_PRICE = new Money(new BigDecimal("25.45"));
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 11, 5, 8, 45, 21, 0, UTC);

    private final PaymentMessagingMapper paymentMessagingMapper = Mappers.getMapper(PaymentMessagingMapper.class);

    @Test
    void paymentCompletedEvent_paymentEventToPaymentResponseAvroModel_paymentResponse() {
        Payment payment = createPayment(PaymentStatus.COMPLETED);
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(payment, TEST_ZONE_DATE_TIME, null);

        PaymentResponse paymentResponse = paymentMessagingMapper.paymentEventToPaymentResponseAvroModel(paymentCompletedEvent);

        assertNotNull(paymentResponse.getId());
        assertTrue(paymentResponse.getSagaId().isEmpty());
        assertEquals(paymentCompletedEvent.getPayment().getCustomerId().getValue().toString(), paymentResponse.getCustomerId());
        assertEquals(paymentCompletedEvent.getPayment().getId().getValue().toString(), paymentResponse.getPaymentId());
        assertEquals(paymentCompletedEvent.getPayment().getOrderId().getValue().toString(), paymentResponse.getOrderId());
        assertEquals(paymentCompletedEvent.getPayment().getPrice().getAmount(), paymentResponse.getPrice());
        assertEquals(paymentCompletedEvent.getPayment().getPaymentStatus().toString(), paymentResponse.getPaymentStatus().toString());
        assertEquals(paymentCompletedEvent.getCreatedAt().toInstant(), paymentResponse.getCreatedAt());
    }

    @Test
    void paymentCancelledEvent_paymentEventToPaymentResponseAvroModel_paymentResponse() {
        Payment payment = createPayment(PaymentStatus.CANCELLED);
        PaymentCancelledEvent paymentCancelledEvent = new PaymentCancelledEvent(payment, TEST_ZONE_DATE_TIME, null);

        PaymentResponse paymentResponse = paymentMessagingMapper.paymentEventToPaymentResponseAvroModel(paymentCancelledEvent);

        assertNotNull(paymentResponse.getId());
        assertTrue(paymentResponse.getSagaId().isEmpty());
        assertEquals(paymentCancelledEvent.getPayment().getCustomerId().getValue().toString(), paymentResponse.getCustomerId());
        assertEquals(paymentCancelledEvent.getPayment().getId().getValue().toString(), paymentResponse.getPaymentId());
        assertEquals(paymentCancelledEvent.getPayment().getOrderId().getValue().toString(), paymentResponse.getOrderId());
        assertEquals(paymentCancelledEvent.getPayment().getPrice().getAmount(), paymentResponse.getPrice());
        assertEquals(paymentCancelledEvent.getPayment().getPaymentStatus().toString(), paymentResponse.getPaymentStatus().toString());
        assertEquals(paymentCancelledEvent.getCreatedAt().toInstant(), paymentResponse.getCreatedAt());
    }

    @Test
    void paymentFailedEvent_paymentEventToPaymentResponseAvroModel_paymentResponse() {
        Payment payment = createPayment(PaymentStatus.FAILED);
        List<String> failureMessages = List.of("fail1", "fail2", "fail3");
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(payment, TEST_ZONE_DATE_TIME, failureMessages, null);

        PaymentResponse paymentResponse = paymentMessagingMapper.paymentEventToPaymentResponseAvroModel(paymentFailedEvent);

        assertNotNull(paymentResponse.getId());
        assertTrue(paymentResponse.getSagaId().isEmpty());
        assertEquals(paymentFailedEvent.getPayment().getCustomerId().getValue().toString(), paymentResponse.getCustomerId());
        assertEquals(paymentFailedEvent.getPayment().getId().getValue().toString(), paymentResponse.getPaymentId());
        assertEquals(paymentFailedEvent.getPayment().getOrderId().getValue().toString(), paymentResponse.getOrderId());
        assertEquals(paymentFailedEvent.getPayment().getPrice().getAmount(), paymentResponse.getPrice());
        assertEquals(paymentFailedEvent.getPayment().getPaymentStatus().toString(), paymentResponse.getPaymentStatus().toString());
        assertEquals(paymentFailedEvent.getCreatedAt().toInstant(), paymentResponse.getCreatedAt());
        assertThat(paymentResponse.getFailureMessages()).contains("fail3", "fail2", "fail1");
    }

    @Test
    void paymentRequest_paymentRequestAvroModelToPaymentRequest_paymentRequest() {
        com.food.ordering.kafka.order.model.PaymentRequest paymentRequestAvroModel =
                com.food.ordering.kafka.order.model.PaymentRequest.newBuilder()
                        .setId(TEST_PAYMENT_REQUEST_EVENT_ID.toString())
                        .setSagaId("")
                        .setOrderId(TEST_ORDER_ID.toString())
                        .setCustomerId(TEST_CUSTOMER_ID.toString())
                        .setPrice(TEST_PRICE.getAmount())
                        .setCreatedAt(TEST_ZONE_DATE_TIME.toInstant())
                        .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                        .build();

        PaymentRequest paymentRequest = paymentMessagingMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel);

        assertEquals(paymentRequestAvroModel.getId(), paymentRequest.getId());
        assertEquals(paymentRequestAvroModel.getSagaId(), paymentRequest.getSagaId());
        assertEquals(paymentRequestAvroModel.getCustomerId(), paymentRequest.getCustomerId());
        assertEquals(paymentRequestAvroModel.getOrderId(), paymentRequest.getOrderId());
        assertEquals(paymentRequestAvroModel.getPrice(), paymentRequest.getPrice());
        assertEquals(paymentRequestAvroModel.getPaymentOrderStatus().toString(), paymentRequest.getPaymentOrderStatus().toString());
        assertEquals(paymentRequestAvroModel.getCreatedAt(), paymentRequest.getCreatedAt());
    }

    private static Payment createPayment(PaymentStatus paymentStatus) {
        return Payment.builder()
                .paymentId(TEST_PAYMENT_ID)
                .customerId(TEST_CUSTOMER_ID)
                .orderId(TEST_ORDER_ID)
                .price(TEST_PRICE)
                .paymentStatus(paymentStatus)
                .build();
    }
}