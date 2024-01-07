package com.food.ordering.restaurant.data.access.restaurant.outbox.mapper;

import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.data.access.restaurant.outbox.entity.OrderOutboxEntity;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderOutboxEntityMapperTest {

    private final static UUID TEST_SAGA_ID = UUID.fromString("0e32015f-74d3-4143-9cd5-d553b18d2320");
    private final static OutboxStatus TEST_OUTBOX_STATUS = OutboxStatus.STARTED;
    private static final OrderApprovalStatus TEST_ORDER_APPROVAL_STATUS = OrderApprovalStatus.APPROVED;
    private final static int TEST_ENTITY_VERSION = 1;
    private final static ZonedDateTime TEST_ZONE_DATE_TIME_1 = ZonedDateTime.of(2023, 11, 4, 11, 5, 0, 0, ZoneOffset.UTC);
    private final static ZonedDateTime TEST_ZONE_DATE_TIME_2 = ZonedDateTime.of(2023, 11, 4, 11, 0, 0, 0, ZoneOffset.UTC);

    private final OrderOutboxEntityMapper orderOutboxEntityMapper = Mappers.getMapper(OrderOutboxEntityMapper.class);

    @Test
    void orderOutboxMessage_orderOutboxMessageToOrderOutboxEntity_orderOutboxEntity() {
        OrderOutboxMessage orderOutboxMessage = OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(TEST_SAGA_ID)
                .createdAt(TEST_ZONE_DATE_TIME_1)
                .processedAt(TEST_ZONE_DATE_TIME_2)
                .type(ORDER_SAGA_NAME)
                .payload("restaurant orderOutboxMessage test payload")
                .outboxStatus(TEST_OUTBOX_STATUS)
                .approvalStatus(TEST_ORDER_APPROVAL_STATUS)
                .version(TEST_ENTITY_VERSION)
                .build();

        OrderOutboxEntity orderOutboxEntity = orderOutboxEntityMapper.orderOutboxMessageToOrderOutboxEntity(orderOutboxMessage);

        assertEquals(orderOutboxMessage.getId(), orderOutboxEntity.getId());
        assertEquals(orderOutboxMessage.getSagaId(), orderOutboxEntity.getSagaId());
        assertEquals(orderOutboxMessage.getCreatedAt(), orderOutboxEntity.getCreatedAt());
        assertEquals(orderOutboxMessage.getProcessedAt(), orderOutboxEntity.getProcessedAt());
        assertEquals(orderOutboxMessage.getType(), orderOutboxEntity.getType());
        assertEquals(orderOutboxMessage.getPayload(), orderOutboxEntity.getPayload());
        assertEquals(orderOutboxMessage.getOutboxStatus(), orderOutboxEntity.getOutboxStatus());
        assertEquals(orderOutboxMessage.getApprovalStatus(), orderOutboxEntity.getApprovalStatus());
        assertEquals(orderOutboxMessage.getVersion(), orderOutboxEntity.getVersion());
    }

    @Test
    void orderOutboxEntity_orderOutboxEntityToOrderOutboxMessage_orderOutboxMessage() {
        OrderOutboxEntity orderOutboxEntity = OrderOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(TEST_SAGA_ID)
                .createdAt(TEST_ZONE_DATE_TIME_1)
                .processedAt(TEST_ZONE_DATE_TIME_2)
                .type(ORDER_SAGA_NAME)
                .payload("restaurant orderOutboxEntity test payload")
                .outboxStatus(TEST_OUTBOX_STATUS)
                .approvalStatus(TEST_ORDER_APPROVAL_STATUS)
                .version(TEST_ENTITY_VERSION)
                .build();

        OrderOutboxMessage orderOutboxMessage = orderOutboxEntityMapper.orderOutboxEntityToOrderOutboxMessage(orderOutboxEntity);

        assertEquals(orderOutboxEntity.getId(), orderOutboxMessage.getId());
        assertEquals(orderOutboxEntity.getSagaId(), orderOutboxMessage.getSagaId());
        assertEquals(orderOutboxEntity.getCreatedAt(), orderOutboxMessage.getCreatedAt());
        assertEquals(orderOutboxEntity.getProcessedAt(), orderOutboxMessage.getProcessedAt());
        assertEquals(orderOutboxEntity.getType(), orderOutboxMessage.getType());
        assertEquals(orderOutboxEntity.getPayload(), orderOutboxMessage.getPayload());
        assertEquals(orderOutboxEntity.getOutboxStatus(), orderOutboxMessage.getOutboxStatus());
        assertEquals(orderOutboxEntity.getApprovalStatus(), orderOutboxMessage.getApprovalStatus());
        assertEquals(orderOutboxEntity.getVersion(), orderOutboxMessage.getVersion());
    }
}