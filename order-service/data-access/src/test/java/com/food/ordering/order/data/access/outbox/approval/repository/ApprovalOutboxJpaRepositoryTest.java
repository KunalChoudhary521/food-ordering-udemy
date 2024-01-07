package com.food.ordering.order.data.access.outbox.approval.repository;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {ApprovalOutboxJpaRepository.class})
@EnableJpaRepositories(basePackages = "com.food.ordering.order.data.access")
@EntityScan(basePackages = "com.food.ordering.order.data.access")
@ActiveProfiles("test")
@DataJpaTest
class ApprovalOutboxJpaRepositoryTest {

    private static final UUID UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Autowired
    private ApprovalOutboxJpaRepository approvalOutboxJpaRepository;

    @Test
    void typeAndOutboxStatusAndSagaStatuses_findByTypeAndOutboxStatusAndSagaStatusIn_returnEntities() {
        OutboxStatus outboxStatus = OutboxStatus.COMPLETED;
        SagaStatus sagaStatus = SagaStatus.SUCCEEDED;

        Optional<List<ApprovalOutboxEntity>> approvalOutboxEntitiesOpt =
                approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, List.of(sagaStatus));

        assertTrue(approvalOutboxEntitiesOpt.isPresent());
        List<ApprovalOutboxEntity> approvalOutboxEntities = approvalOutboxEntitiesOpt.get();
        assertEquals(2, approvalOutboxEntities.size());

        assertEquals(UUID_1, approvalOutboxEntities.get(0).getId());
        assertEquals(UUID_1, approvalOutboxEntities.get(0).getSagaId());
        assertEquals(ZonedDateTime.of(2023, 10, 13, 14, 0, 0, 0, ZoneOffset.UTC), approvalOutboxEntities.get(0).getCreatedAt());
        assertEquals(ZonedDateTime.of(2023, 10, 13, 14, 1, 0, 0, ZoneOffset.UTC), approvalOutboxEntities.get(0).getProcessedAt());
        assertEquals(ORDER_SAGA_NAME, approvalOutboxEntities.get(0).getType());
        assertEquals("restaurant_approval_outbox db test payload", approvalOutboxEntities.get(0).getPayload());
        assertEquals(sagaStatus, approvalOutboxEntities.get(0).getSagaStatus());
        assertEquals(OrderStatus.APPROVED, approvalOutboxEntities.get(0).getOrderStatus());
        assertEquals(outboxStatus, approvalOutboxEntities.get(0).getOutboxStatus());
        assertEquals(2, approvalOutboxEntities.get(0).getVersion());
    }

    @Test
    void typeAndSagaIdAndSagaStatuses_findByTypeAndSagaIdAndSagaStatusIn_returnEntities() {
        SagaStatus sagaStatus = SagaStatus.SUCCEEDED;

        Optional<ApprovalOutboxEntity> approvalOutboxEntityOpt =
                approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, UUID_2, List.of(sagaStatus));

        assertTrue(approvalOutboxEntityOpt.isPresent());

        ApprovalOutboxEntity approvalOutboxEntity = approvalOutboxEntityOpt.get();
        assertEquals(UUID_2, approvalOutboxEntity.getId());
        assertEquals(UUID_2, approvalOutboxEntity.getSagaId());
        assertEquals(ZonedDateTime.of(2023, 11, 23, 15, 0, 0, 0, ZoneOffset.UTC), approvalOutboxEntity.getCreatedAt());
        assertEquals(ZonedDateTime.of(2023, 11, 23, 15, 1, 0, 0, ZoneOffset.UTC), approvalOutboxEntity.getProcessedAt());//update to SB 3.2 and replace UTC_ZONE_ID with ZoneOffset.UTC
        assertEquals(ORDER_SAGA_NAME, approvalOutboxEntity.getType());
        assertEquals("restaurant_approval_outbox db test payload", approvalOutboxEntity.getPayload());
        assertEquals(sagaStatus, approvalOutboxEntity.getSagaStatus());
        assertEquals(OrderStatus.APPROVED, approvalOutboxEntity.getOrderStatus());
        assertEquals(OutboxStatus.COMPLETED, approvalOutboxEntity.getOutboxStatus());
        assertEquals(3, approvalOutboxEntity.getVersion());
    }

    @Test
    void typeAndOutboxStatusAndSagaStatuses_deleteByTypeAndOutboxStatusAndSagaStatusIn_returnEntities() {
        OutboxStatus outboxStatus = OutboxStatus.COMPLETED;
        SagaStatus sagaStatus = SagaStatus.SUCCEEDED;

        approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, List.of(sagaStatus));

        Optional<List<ApprovalOutboxEntity>> approvalOutboxEntitiesOpt =
                approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(ORDER_SAGA_NAME, outboxStatus, List.of(sagaStatus));

        assertTrue(approvalOutboxEntitiesOpt.isPresent()); // TODO: change method signature to List<...>
        assertTrue(approvalOutboxEntitiesOpt.get().isEmpty());
    }
}