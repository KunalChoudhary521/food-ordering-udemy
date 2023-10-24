package com.food.ordering.order.messaging.listener.kafka;


import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.kafka.order.model.PaymentStatus;
import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.port.output.repository.OrderRepository;
import com.food.ordering.saga.SagaStatus;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "kafka-config.bootstrap-servers=localhost:9093")
@DirtiesContext
@EmbeddedKafka(topics = {"${order-service.payment-response-topic}"}, partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PaymentResponseKafkaListenerTest {

    private static final UUID TEST_SAGA_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private static final UUID TEST_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID PAYMENT_OUTBOX_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");

    @Value("${order-service.payment-response-topic}")
    private String paymentResponseTopic;

    @Value("${kafka-consumer-config.payment-consumer-group-id}")
    private String paymentConsumerGroupId;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, PaymentResponse> consumerFactory;

    @Autowired
    private ProducerFactory<String, PaymentResponse> producerFactory;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    private KafkaTemplate<String, PaymentResponse> kafkaTemplate;

    private Consumer<String, PaymentResponse> consumer;

    @BeforeAll
    public void setUp() {
        consumer = consumerFactory.createConsumer(paymentConsumerGroupId, "");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, paymentResponseTopic);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @AfterAll
    public void tearDown() {
        consumer.close();
    }

    @Test
    void completedPaymentResponseAvroModel_receive_processMessageAndUpdateOutboxMessage() {
        PaymentResponse paymentResponse = PaymentResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(TEST_SAGA_ID.toString())
                .setOrderId(TEST_ORDER_ID.toString())
                .setPaymentId(UUID.randomUUID().toString())
                .setCustomerId(UUID.randomUUID().toString())
                .setPrice(new BigDecimal("56.23"))
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .setPaymentStatus(PaymentStatus.COMPLETED)
                .setFailureMessages(List.of())
                .build();

        ProducerRecord<String, PaymentResponse> paymentResponseRecord = new ProducerRecord<>(paymentResponseTopic, TEST_SAGA_ID.toString(), paymentResponse);

        kafkaTemplate.send(paymentResponseRecord);
        kafkaTemplate.flush();

        KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2L), 1);

        Optional<Order> order = orderRepository.findById(new OrderId(TEST_ORDER_ID));
        assertTrue(order.isPresent());
        assertEquals(OrderStatus.PAID, order.get().getOrderStatus());

        Optional<PaymentOutboxEntity> processedOutboxMessage = paymentOutboxJpaRepository.findById(PAYMENT_OUTBOX_ID);
        assertTrue(processedOutboxMessage.isPresent());
        assertEquals(OrderStatus.PAID, processedOutboxMessage.get().getOrderStatus());
        assertEquals(SagaStatus.PROCESSING, processedOutboxMessage.get().getSagaStatus());
    }
}
