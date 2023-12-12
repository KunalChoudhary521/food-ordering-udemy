package com.food.ordering.order.domain.outbox.scheduler.payment;

import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
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
class PaymentOutboxSchedulerTest {

    private static final UUID PAYMENT_OUTBOX_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    @Value("${order-service.payment-request-topic}")
    private String paymentRequestTopic;

    @MockBean
    private KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private PaymentOutboxScheduler paymentOutboxScheduler;

    @Test
    void orderPaymentOutboxMessage_processOutboxMessage_publishMessageAndUpdateOutboxStatus() {
        CompletableFuture<SendResult<String, PaymentRequest>> futureResult = CompletableFuture.completedFuture(createSendResult());

        when(kafkaTemplate.send(eq(paymentRequestTopic), anyString(), any(PaymentRequest.class))).thenReturn(futureResult);

        paymentOutboxScheduler.processOutboxMessage();

        Optional<PaymentOutboxEntity> processedOutboxMessage = paymentOutboxJpaRepository.findById(PAYMENT_OUTBOX_ID);
        assertTrue(processedOutboxMessage.isPresent());
        assertEquals(OutboxStatus.COMPLETED, processedOutboxMessage.get().getOutboxStatus());
    }

    private SendResult<String, PaymentRequest> createSendResult() {
        ProducerRecord<String, PaymentRequest> producerRecord = new ProducerRecord<>("", "", new PaymentRequest());
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("", 1), 1L, 1, 1L, 1, 1);
        return new SendResult<>(producerRecord, recordMetadata);
    }
}