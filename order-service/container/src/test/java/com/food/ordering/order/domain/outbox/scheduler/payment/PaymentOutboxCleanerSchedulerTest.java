package com.food.ordering.order.domain.outbox.scheduler.payment;

import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.outbox.OutboxStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PaymentOutboxCleanerSchedulerTest {

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private PaymentOutboxCleanerScheduler paymentOutboxCleanerScheduler;

    @Test
    void leftoverOutboxMessages_processOutboxMessage_deleteCompletedOutboxMessages() {
        paymentOutboxCleanerScheduler.processOutboxMessage();

        List<PaymentOutboxEntity> remainingOutboxEntities = paymentOutboxJpaRepository.findAll();

        remainingOutboxEntities.forEach(paymentOutboxEntity ->
                assertNotEquals(OutboxStatus.COMPLETED, paymentOutboxEntity.getOutboxStatus()));
    }
}