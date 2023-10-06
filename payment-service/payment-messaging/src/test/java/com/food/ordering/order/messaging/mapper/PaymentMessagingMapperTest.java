package com.food.ordering.order.messaging.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.kafka.order.model.PaymentOrderStatus;
import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.outbox.model.OrderEventPayload;
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

class PaymentMessagingMapperTest {

    private static final UUID TEST_PAYMENT_REQUEST_EVENT_ID = UUID.fromString("af71693e-df85-43b0-84bf-5de058a8d1e8");
    private static final PaymentId TEST_PAYMENT_ID = new PaymentId(UUID.fromString("22f1ec47-d440-4d1c-8ee4-9b312317a3a1"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("7ff24312-78c6-4dcf-8bb6-7a2cf07f0137"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("ac9ff3b1-5d70-4202-97a2-cb25030f9f3a"));
    private static final Money TEST_PRICE = new Money(new BigDecimal("25.45"));
    private static final UUID TEST_SAGA_ID = UUID.fromString("ad128995-5624-4556-af9d-bbc3262b93f7");
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 11, 5, 8, 45, 21, 0, UTC);

    private final PaymentMessagingMapper paymentMessagingMapper = Mappers.getMapper(PaymentMessagingMapper.class);

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

    @Test
    void orderEventPayload_orderEventPayloadToPaymentResponseAvroModel_paymentResponse() {
        OrderEventPayload orderEventPayload = OrderEventPayload.builder()
                .paymentId(TEST_PAYMENT_ID.getValue().toString())
                .customerId(TEST_CUSTOMER_ID.getValue().toString())
                .orderId(TEST_ORDER_ID.getValue().toString())
                .price(TEST_PRICE.getAmount())
                .createdAt(TEST_ZONE_DATE_TIME)
                .paymentStatus("FAILED")
                .failureMessages(List.of("fail1", "fail2", "fail3"))
                .build();

        PaymentResponse paymentResponse =
                paymentMessagingMapper.orderEventPayloadToPaymentResponseAvroModel(TEST_SAGA_ID.toString(), orderEventPayload);

        assertNotNull(paymentResponse.getId());
        assertEquals(TEST_SAGA_ID.toString(), paymentResponse.getSagaId());
        assertEquals(orderEventPayload.getPaymentId(), paymentResponse.getPaymentId());
        assertEquals(orderEventPayload.getCustomerId(), paymentResponse.getCustomerId());
        assertEquals(orderEventPayload.getOrderId(), paymentResponse.getOrderId());
        assertEquals(orderEventPayload.getPrice(), paymentResponse.getPrice());
        assertEquals(orderEventPayload.getCreatedAt(), ZonedDateTime.ofInstant(paymentResponse.getCreatedAt(), UTC));
        assertEquals(orderEventPayload.getPaymentStatus(), paymentResponse.getPaymentStatus().toString());
        assertThat(paymentResponse.getFailureMessages()).containsAll(orderEventPayload.getFailureMessages());
    }
}