package com.food.ordering.order.messaging.listener.kafka;

import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.kafka.order.model.PaymentStatus;
import com.food.ordering.order.container.OrderServiceApplication;
import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.order.data.access.outbox.approval.repository.ApprovalOutboxJpaRepository;
import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.port.output.repository.OrderRepository;
import com.food.ordering.saga.SagaStatus;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "${order-service.payment-response-topic}", partitions = 1,
        bootstrapServersProperty = "kafka-config.bootstrap-servers")
@ActiveProfiles("test")
class PaymentResponseKafkaListenerTest {

    private static final UUID ORDER_PAID_SAGA_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private static final UUID ORDER_CANCELLED_SAGA_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
    private static final UUID ORDER_PAID_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID ORDER_CANCELLED_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");

    @Value("${order-service.payment-response-topic}")
    private String paymentResponseTopic;

    @Autowired
    private KafkaTemplate<String, PaymentResponse> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private ApprovalOutboxJpaRepository approvalOutboxJpaRepository;

    @Test
    void completedPaymentResponseAvroModel_receive_orderPaidAndSagaProcessing() {
        PaymentResponse completedPaymentResponse = createPaymentResponse(ORDER_PAID_ID.toString(), PaymentStatus.COMPLETED,
                new ArrayList<>(), ORDER_PAID_SAGA_ID.toString());

        kafkaTemplate.send(paymentResponseTopic, completedPaymentResponse);

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<Order> order = orderRepository.findById(new OrderId(ORDER_PAID_ID));
            assertTrue(order.isPresent());
            assertEquals(OrderStatus.PAID, order.get().getOrderStatus());

            Optional<PaymentOutboxEntity> paymentOutboxEntity =
                    paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, ORDER_PAID_SAGA_ID, List.of(SagaStatus.PROCESSING));
            assertTrue(paymentOutboxEntity.isPresent());
            assertEquals(OrderStatus.PAID, paymentOutboxEntity.get().getOrderStatus());

            Optional<ApprovalOutboxEntity> approvalOutboxEntity =
                    approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, ORDER_PAID_SAGA_ID, List.of(SagaStatus.PROCESSING));
            assertTrue(approvalOutboxEntity.isPresent());
            assertEquals(OrderStatus.PAID, approvalOutboxEntity.get().getOrderStatus());
        });
    }

    @Test
    void cancelledPaymentResponseAvroModel_receive_orderCancelledAndSagaCompensated() {
        List<String> failureMessages = new ArrayList<>();
        failureMessages.add("fail1");
        PaymentResponse failedPaymentResponse = createPaymentResponse(ORDER_CANCELLED_ID.toString(), PaymentStatus.CANCELLED,
                failureMessages, ORDER_CANCELLED_SAGA_ID.toString());

        kafkaTemplate.send(paymentResponseTopic, failedPaymentResponse);

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<Order> order = orderRepository.findById(new OrderId(ORDER_CANCELLED_ID));
            assertTrue(order.isPresent());
            assertEquals(OrderStatus.CANCELLED, order.get().getOrderStatus());

            Optional<PaymentOutboxEntity> paymentOutboxEntity = paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME,
                    ORDER_CANCELLED_SAGA_ID, List.of(SagaStatus.COMPENSATED));
            assertTrue(paymentOutboxEntity.isPresent());
            assertEquals(OrderStatus.CANCELLED, paymentOutboxEntity.get().getOrderStatus());

            Optional<ApprovalOutboxEntity> approvalOutboxEntity = approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME,
                    ORDER_CANCELLED_SAGA_ID, List.of(SagaStatus.COMPENSATED));
            assertTrue(approvalOutboxEntity.isPresent());
            assertEquals(OrderStatus.CANCELLED, approvalOutboxEntity.get().getOrderStatus());
        });
    }

    private PaymentResponse createPaymentResponse(String orderId, PaymentStatus paymentStatus, List<String> failureMessages, String sagaId) {
        return PaymentResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderId)
                .setPaymentId(UUID.randomUUID().toString())
                .setCustomerId(UUID.randomUUID().toString())
                .setPrice(new BigDecimal("56.23"))
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .setPaymentStatus(paymentStatus)
                .setFailureMessages(failureMessages)
                .build();
    }
}
