package com.food.ordering.order.data.access.outbox.approval.mapper;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApprovalOutboxEntityMapperTest {

    private final static UUID TEST_SAGA_ID = UUID.fromString("f59425a0-2d68-4155-97fd-b702d73612b2");
    private final static OrderStatus TEST_ORDER_STATUS = OrderStatus.APPROVED;
    private final static SagaStatus TEST_SAGA_STATUS = SagaStatus.STARTED;
    private final static OutboxStatus TEST_OUTBOX_STATUS = OutboxStatus.STARTED;
    private final static int TEST_ENTITY_VERSION = 1;
    private final static ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 10, 3, 9, 4, 9, 12, ZoneOffset.UTC);

    private final ApprovalOutboxEntityMapper approvalOutboxEntityMapper = Mappers.getMapper(ApprovalOutboxEntityMapper.class);

    @Test
    void orderApprovalOutboxMessage_orderApprovalOutboxMessageToApprovalOutboxEntity_approvalOutboxEntity() {
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(TEST_SAGA_ID)
                .createdAt(TEST_ZONE_DATE_TIME)
                .type(ORDER_SAGA_NAME)
                .payload("orderApprovalOutboxMessage test payload")
                .orderStatus(TEST_ORDER_STATUS)
                .sagaStatus(TEST_SAGA_STATUS)
                .outboxStatus(TEST_OUTBOX_STATUS)
                .version(TEST_ENTITY_VERSION)
                .build();

        ApprovalOutboxEntity approvalOutboxEntity = approvalOutboxEntityMapper.orderApprovalOutboxMessageToApprovalOutboxEntity(orderApprovalOutboxMessage);

        assertEquals(orderApprovalOutboxMessage.getId(), approvalOutboxEntity.getId());
        assertEquals(orderApprovalOutboxMessage.getSagaId(), approvalOutboxEntity.getSagaId());
        assertEquals(orderApprovalOutboxMessage.getCreatedAt(), approvalOutboxEntity.getCreatedAt());
        assertEquals(orderApprovalOutboxMessage.getType(), approvalOutboxEntity.getType());
        assertEquals(orderApprovalOutboxMessage.getPayload(), approvalOutboxEntity.getPayload());
        assertEquals(orderApprovalOutboxMessage.getOrderStatus(), approvalOutboxEntity.getOrderStatus());
        assertEquals(orderApprovalOutboxMessage.getSagaStatus(), approvalOutboxEntity.getSagaStatus());
        assertEquals(orderApprovalOutboxMessage.getOutboxStatus(), approvalOutboxEntity.getOutboxStatus());
        assertEquals(orderApprovalOutboxMessage.getVersion(), approvalOutboxEntity.getVersion());
    }

    @Test
    void approvalOutboxEntity_approvalOutboxEntityToOrderApprovalOutboxMessage_orderApprovalOutboxMessage() {

        ApprovalOutboxEntity approvalOutboxEntity = ApprovalOutboxEntity.builder()
                .id(UUID.randomUUID())
                .sagaId(TEST_SAGA_ID)
                .createdAt(TEST_ZONE_DATE_TIME)
                .type(ORDER_SAGA_NAME)
                .payload("approvalOutboxEntity test payload")
                .orderStatus(TEST_ORDER_STATUS)
                .sagaStatus(TEST_SAGA_STATUS)
                .outboxStatus(TEST_OUTBOX_STATUS)
                .version(TEST_ENTITY_VERSION)
                .build();

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = approvalOutboxEntityMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(approvalOutboxEntity);

        assertEquals(approvalOutboxEntity.getId(), orderApprovalOutboxMessage.getId());
        assertEquals(approvalOutboxEntity.getSagaId(), orderApprovalOutboxMessage.getSagaId());
        assertEquals(approvalOutboxEntity.getCreatedAt(), orderApprovalOutboxMessage.getCreatedAt());
        assertEquals(approvalOutboxEntity.getType(), orderApprovalOutboxMessage.getType());
        assertEquals(approvalOutboxEntity.getPayload(), orderApprovalOutboxMessage.getPayload());
        assertEquals(approvalOutboxEntity.getOrderStatus(), orderApprovalOutboxMessage.getOrderStatus());
        assertEquals(approvalOutboxEntity.getSagaStatus(), orderApprovalOutboxMessage.getSagaStatus());
        assertEquals(approvalOutboxEntity.getOutboxStatus(), orderApprovalOutboxMessage.getOutboxStatus());
        assertEquals(approvalOutboxEntity.getVersion(), orderApprovalOutboxMessage.getVersion());
    }
}