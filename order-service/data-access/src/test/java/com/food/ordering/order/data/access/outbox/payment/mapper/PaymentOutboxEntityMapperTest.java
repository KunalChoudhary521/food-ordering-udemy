package com.food.ordering.order.data.access.outbox.payment.mapper;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentOutboxEntityMapperTest {

    private final static UUID TEST_SAGA_ID = UUID.fromString("acf1ce59-c42f-4746-b580-609a5d459ab0");
    private final static OrderStatus TEST_ORDER_STATUS = OrderStatus.APPROVED;
    private final static SagaStatus TEST_SAGA_STATUS = SagaStatus.STARTED;
    private final static OutboxStatus TEST_OUTBOX_STATUS = OutboxStatus.STARTED;
    private final static int TEST_ENTITY_VERSION = 1;
    private final static ZoneId UTC = ZoneId.of("UTC");
    private final static ZonedDateTime TEST_ZONE_DATE_TIME_1 = ZonedDateTime.of(2023, 10, 3, 6, 36, 2, 0, UTC);
    private final static ZonedDateTime TEST_ZONE_DATE_TIME_2 = ZonedDateTime.of(2023, 9, 29, 18, 45, 7, 0, UTC);

    private final PaymentOutboxEntityMapper paymentOutboxEntityMapper = Mappers.getMapper(PaymentOutboxEntityMapper.class);

    @Test
    void orderPaymentOutboxMessage_orderPaymentOutboxMessageToPaymentOutboxEntity_paymentOutboxEntity() {
        OrderPaymentOutboxMessage orderPaymentOutboxMessage = OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(TEST_SAGA_ID)
                .createdAt(TEST_ZONE_DATE_TIME_1)
                .processedAt(TEST_ZONE_DATE_TIME_2)
                .type(ORDER_SAGA_NAME)
                .payload("orderPaymentOutboxMessage test payload")
                .orderStatus(TEST_ORDER_STATUS)
                .sagaStatus(TEST_SAGA_STATUS)
                .outboxStatus(TEST_OUTBOX_STATUS)
                .version(TEST_ENTITY_VERSION)
                .build();

        PaymentOutboxEntity paymentOutboxEntity = paymentOutboxEntityMapper.orderPaymentOutboxMessageToPaymentOutboxEntity(orderPaymentOutboxMessage);

        assertEquals(orderPaymentOutboxMessage.getId(), paymentOutboxEntity.getId());
        assertEquals(orderPaymentOutboxMessage.getSagaId(), paymentOutboxEntity.getSagaId());
        assertEquals(orderPaymentOutboxMessage.getCreatedAt(), paymentOutboxEntity.getCreatedAt());
        assertEquals(orderPaymentOutboxMessage.getProcessedAt(), paymentOutboxEntity.getProcessedAt());
        assertEquals(orderPaymentOutboxMessage.getType(), paymentOutboxEntity.getType());
        assertEquals(orderPaymentOutboxMessage.getPayload(), paymentOutboxEntity.getPayload());
        assertEquals(orderPaymentOutboxMessage.getOrderStatus(), paymentOutboxEntity.getOrderStatus());
        assertEquals(orderPaymentOutboxMessage.getSagaStatus(), paymentOutboxEntity.getSagaStatus());
        assertEquals(orderPaymentOutboxMessage.getOutboxStatus(), paymentOutboxEntity.getOutboxStatus());
        assertEquals(orderPaymentOutboxMessage.getVersion(), paymentOutboxEntity.getVersion());
    }

    @Test
    void paymentOutboxEntity_paymentOutboxEntityToOrderPaymentOutboxMessage_orderPaymentOutboxMessage() {
        PaymentOutboxEntity paymentOutboxEntity = PaymentOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(TEST_SAGA_ID)
                .createdAt(TEST_ZONE_DATE_TIME_1)
                .processedAt(TEST_ZONE_DATE_TIME_2)
                .type(ORDER_SAGA_NAME)
                .payload("paymentOutboxEntity test payload")
                .orderStatus(TEST_ORDER_STATUS)
                .sagaStatus(TEST_SAGA_STATUS)
                .outboxStatus(TEST_OUTBOX_STATUS)
                .version(TEST_ENTITY_VERSION)
                .build();

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = paymentOutboxEntityMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(paymentOutboxEntity);

        assertEquals(paymentOutboxEntity.getId(), orderPaymentOutboxMessage.getId());
        assertEquals(paymentOutboxEntity.getSagaId(), orderPaymentOutboxMessage.getSagaId());
        assertEquals(paymentOutboxEntity.getCreatedAt(), orderPaymentOutboxMessage.getCreatedAt());
        assertEquals(paymentOutboxEntity.getProcessedAt(), orderPaymentOutboxMessage.getProcessedAt());
        assertEquals(paymentOutboxEntity.getType(), orderPaymentOutboxMessage.getType());
        assertEquals(paymentOutboxEntity.getPayload(), orderPaymentOutboxMessage.getPayload());
        assertEquals(paymentOutboxEntity.getOrderStatus(), orderPaymentOutboxMessage.getOrderStatus());
        assertEquals(paymentOutboxEntity.getSagaStatus(), orderPaymentOutboxMessage.getSagaStatus());
        assertEquals(paymentOutboxEntity.getOutboxStatus(), orderPaymentOutboxMessage.getOutboxStatus());
        assertEquals(paymentOutboxEntity.getVersion(), orderPaymentOutboxMessage.getVersion());
    }
}