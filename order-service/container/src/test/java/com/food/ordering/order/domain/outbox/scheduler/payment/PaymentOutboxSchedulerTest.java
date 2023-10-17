package com.food.ordering.order.domain.outbox.scheduler.payment;

import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.outbox.OutboxStatus;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(topics = "${order-service.payment-request-topic}", partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PaymentOutboxSchedulerTest {

    private static final UUID PAYMENT_OUTBOX_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    @Value("${order-service.payment-request-topic}")
    private String paymentRequestTopic;

    @Value("${kafka-consumer-config.payment-consumer-group-id}")
    private String paymentConsumerGroupId;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, PaymentRequest> consumerFactory;  // kafka properties used from KafkaConsumerConfig

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private PaymentOutboxScheduler paymentOutboxScheduler;

    private Consumer<String, PaymentRequest> consumer;

    @BeforeAll
    public void setUp() {
        consumer = consumerFactory.createConsumer(paymentConsumerGroupId, "");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, paymentRequestTopic);
    }

    @AfterAll
    public void tearDown() {
        consumer.close();
    }

    @Test
    void orderPaymentOutboxMessage_processOutboxMessage_publishMessageAndUpdateOutboxStatus() {
        paymentOutboxScheduler.processOutboxMessage();
        KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2L), 1);

        Optional<PaymentOutboxEntity> processedOutboxMessage = paymentOutboxJpaRepository.findById(PAYMENT_OUTBOX_ID);

        assertTrue(processedOutboxMessage.isPresent());
        assertEquals(OutboxStatus.COMPLETED, processedOutboxMessage.get().getOutboxStatus());
    }
}