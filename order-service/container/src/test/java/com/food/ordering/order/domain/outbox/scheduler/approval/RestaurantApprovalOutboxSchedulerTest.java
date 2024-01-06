package com.food.ordering.order.domain.outbox.scheduler.approval;

import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.order.data.access.outbox.approval.repository.ApprovalOutboxJpaRepository;
import com.food.ordering.outbox.OutboxStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestaurantApprovalOutboxSchedulerTest {

    private static final UUID RESTAURANT_APPROVAL_OUTBOX_ID = UUID.fromString("00000000-0000-0000-0000-000000000050");

    @Value("${order-service.restaurant-approval-request-topic}")
    private String restaurantApprovalRequestTopic;

    @MockBean
    private KafkaTemplate<String, RestaurantApprovalRequest> kafkaTemplate;

    @Autowired
    private ApprovalOutboxJpaRepository restaurantApprovalOutboxJpaRepository;

    @Autowired
    private RestaurantApprovalOutboxScheduler restaurantApprovalOutboxScheduler;

    @Test
    void orderApprovalOutboxMessage_processOutboxMessage_publishMessageAndUpdateOutboxStatus() {
        CompletableFuture<SendResult<String, RestaurantApprovalRequest>> futureResult = CompletableFuture.completedFuture(createSendResult());

        when(kafkaTemplate.send(eq(restaurantApprovalRequestTopic), anyString(), any(RestaurantApprovalRequest.class))).thenReturn(futureResult);

        restaurantApprovalOutboxScheduler.processOutboxMessage();

        Optional<ApprovalOutboxEntity> processedOutboxMessage = restaurantApprovalOutboxJpaRepository.findById(RESTAURANT_APPROVAL_OUTBOX_ID);
        assertTrue(processedOutboxMessage.isPresent());
        assertEquals(OutboxStatus.COMPLETED, processedOutboxMessage.get().getOutboxStatus());
    }

    private SendResult<String, RestaurantApprovalRequest> createSendResult() {
        ProducerRecord<String, RestaurantApprovalRequest> producerRecord = new ProducerRecord<>("", "", new RestaurantApprovalRequest());
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("", 1), 1L, 1, 1L, 1, 1);
        return new SendResult<>(producerRecord, recordMetadata);
    }
}
