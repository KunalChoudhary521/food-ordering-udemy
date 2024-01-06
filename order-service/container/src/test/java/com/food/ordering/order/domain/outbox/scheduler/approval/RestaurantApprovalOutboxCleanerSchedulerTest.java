package com.food.ordering.order.domain.outbox.scheduler.approval;

import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.order.data.access.outbox.approval.repository.ApprovalOutboxJpaRepository;
import com.food.ordering.outbox.OutboxStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestaurantApprovalOutboxCleanerSchedulerTest {

    @Autowired
    private ApprovalOutboxJpaRepository restaurantApprovalOutboxJpaRepository;

    @Autowired
    private RestaurantApprovalOutboxCleanerScheduler restaurantApprovalOutboxCleanerScheduler;

    @Test
    void leftoverApprovalOutboxMessages_processOutboxMessage_deleteCompletedOutboxMessages() {
        restaurantApprovalOutboxCleanerScheduler.processOutboxMessage();

        List<ApprovalOutboxEntity> remainingOutboxEntities = restaurantApprovalOutboxJpaRepository.findAll();

        remainingOutboxEntities.forEach(approvalOutboxEntity ->
                assertNotEquals(OutboxStatus.COMPLETED, approvalOutboxEntity.getOutboxStatus()));
    }
}