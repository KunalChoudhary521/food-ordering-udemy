package com.food.ordering.order.messaging.listener.kafka;

import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.kafka.order.model.OrderApprovalStatus;
import com.food.ordering.kafka.order.model.RestaurantApprovalResponse;
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
@EmbeddedKafka(topics = "${order-service.restaurant-approval-response-topic}", partitions = 1,
        bootstrapServersProperty = "kafka-config.bootstrap-servers")
@ActiveProfiles("test")
public class RestaurantApprovalResponseKafkaListenerTest {

    private static final UUID ORDER_APPROVED_SAGA_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
    private static final UUID ORDER_CANCELLING_SAGA_ID = UUID.fromString("00000000-0000-0000-0000-000000000008");
    private static final UUID ORDER_APPROVED_ID = UUID.fromString("00000000-0000-0000-0000-000000000013");
    private static final UUID ORDER_CANCELLING_ID = UUID.fromString("00000000-0000-0000-0000-000000000014");

    @Value("${order-service.restaurant-approval-response-topic}")
    private String restaurantApprovalResponseTopic;

    @Autowired
    private KafkaTemplate<String, RestaurantApprovalResponse> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private ApprovalOutboxJpaRepository approvalOutboxJpaRepository;

    @Test
    void approvedRestaurantResponseAvroModel_receive_orderApprovedAndSagaSucceeded() {
        RestaurantApprovalResponse orderApprovedResponse = createRestaurantApprovalResponse(ORDER_APPROVED_ID.toString(),
                OrderApprovalStatus.APPROVED, new ArrayList<>(), ORDER_APPROVED_SAGA_ID.toString());

        kafkaTemplate.send(restaurantApprovalResponseTopic, orderApprovedResponse);

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<Order> order = orderRepository.findById(new OrderId(ORDER_APPROVED_ID));
            assertTrue(order.isPresent());
            assertEquals(OrderStatus.APPROVED, order.get().getOrderStatus());

            Optional<PaymentOutboxEntity> paymentOutboxEntity =
                    paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, ORDER_APPROVED_SAGA_ID,
                            List.of(SagaStatus.SUCCEEDED));
            assertTrue(paymentOutboxEntity.isPresent());
            assertEquals(OrderStatus.APPROVED, paymentOutboxEntity.get().getOrderStatus());

            Optional<ApprovalOutboxEntity> approvalOutboxEntity =
                    approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, ORDER_APPROVED_SAGA_ID,
                            List.of(SagaStatus.SUCCEEDED));
            assertTrue(approvalOutboxEntity.isPresent());
            assertEquals(OrderStatus.APPROVED, approvalOutboxEntity.get().getOrderStatus());
        });
    }

    @Test
    void rejectedRestaurantResponseAvroModel_receive_orderCancellingAndSagaCompensating() {
        List<String> failureMessages = new ArrayList<>();
        failureMessages.add("fail1");
        RestaurantApprovalResponse orderRejectedResponse = createRestaurantApprovalResponse(ORDER_CANCELLING_ID.toString(),
                OrderApprovalStatus.REJECTED, failureMessages, ORDER_CANCELLING_SAGA_ID.toString());

        kafkaTemplate.send(restaurantApprovalResponseTopic, orderRejectedResponse);

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<Order> order = orderRepository.findById(new OrderId(ORDER_CANCELLING_ID));
            assertTrue(order.isPresent());
            assertEquals(OrderStatus.CANCELLING, order.get().getOrderStatus());

            Optional<PaymentOutboxEntity> paymentOutboxEntity =
                    paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, ORDER_CANCELLING_SAGA_ID,
                            List.of(SagaStatus.COMPENSATING));
            assertTrue(paymentOutboxEntity.isPresent());
            assertEquals(OrderStatus.CANCELLING, paymentOutboxEntity.get().getOrderStatus());

            Optional<ApprovalOutboxEntity> approvalOutboxEntity =
                    approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(ORDER_SAGA_NAME, ORDER_CANCELLING_SAGA_ID,
                            List.of(SagaStatus.COMPENSATING));
            assertTrue(approvalOutboxEntity.isPresent());
            assertEquals(OrderStatus.CANCELLING, approvalOutboxEntity.get().getOrderStatus());
        });
    }

    private RestaurantApprovalResponse createRestaurantApprovalResponse(String orderId, OrderApprovalStatus orderApprovalStatus,
                                                                        List<String> failureMessage, String sagaId) {
        return RestaurantApprovalResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderId)
                .setRestaurantId(UUID.randomUUID().toString())
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .setOrderApprovalStatus(orderApprovalStatus)
                .setFailureMessages(failureMessage)
                .build();
    }
}
