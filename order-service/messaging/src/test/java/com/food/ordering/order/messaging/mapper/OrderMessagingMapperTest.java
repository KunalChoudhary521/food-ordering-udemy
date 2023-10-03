package com.food.ordering.order.messaging.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.kafka.order.model.OrderApprovalStatus;
import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderMessagingMapperTest {

    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("7ff24312-78c6-4dcf-8bb6-7a2cf07f0137"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("ac9ff3b1-5d70-4202-97a2-cb25030f9f3a"));
    private static final RestaurantId TEST_RESTAURANT_ID = new RestaurantId(UUID.fromString("bb830a03-3395-4583-9476-5f99f5515cf3"));
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("4e89072e-2857-4658-97aa-5642ca21bbe6"));
    private static final ProductId TEST_PRODUCT_ID_2 = new ProductId(UUID.fromString("15b429e7-ed12-4d41-b739-1348bab05d50"));
    private static final UUID TEST_SAGA_ID = UUID.fromString("a93d1120-e8f1-403c-9a31-0142cae0e6bb");
    private static final UUID TEST_COMPLETED_PAYMENT_ID = UUID.fromString("96eb41ab-b4c7-4713-9394-34e3b031df60");
    private static final UUID TEST_PAYMENT_RESPONSE_EVENT_ID = UUID.fromString("bcb4a9ad-3590-4443-926b-49761ce34593");
    private static final UUID TEST_RESTAURANT_APPROVAL_RESPONSE_EVENT_ID = UUID.fromString("0fe1b3cf-20a3-458b-b2b7-17ef52fdca25");
    public static final Money TEST_PRICE = new Money(new BigDecimal("17.49"));
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 11, 5, 8, 45, 21, 0, UTC);

    private final OrderMessagingMapper orderMessagingMapper = Mappers.getMapper(OrderMessagingMapper.class);

    @Test
    void paymentResponseAvroModel_paymentResponseAvroModelToPaymentResponse_paymentResponse() {
        com.food.ordering.kafka.order.model.PaymentResponse paymentResponseAvroModel =
                com.food.ordering.kafka.order.model.PaymentResponse.newBuilder()
                        .setId(TEST_PAYMENT_RESPONSE_EVENT_ID.toString())
                        .setSagaId("")
                        .setPaymentId(TEST_COMPLETED_PAYMENT_ID.toString())
                        .setCustomerId(TEST_CUSTOMER_ID.toString())
                        .setOrderId(TEST_ORDER_ID.toString())
                        .setPrice(TEST_PRICE.getAmount())
                        .setCreatedAt(TEST_ZONE_DATE_TIME.toInstant())
                        .setPaymentStatus(com.food.ordering.kafka.order.model.PaymentStatus.COMPLETED)
                        .setFailureMessages(List.of("fail1", "fail2", "fail3"))
                        .build();

        PaymentResponse paymentResponse = orderMessagingMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel);

        assertEquals(paymentResponseAvroModel.getId(), paymentResponse.getId());
        assertEquals(paymentResponseAvroModel.getSagaId(), paymentResponse.getSagaId());
        assertEquals(paymentResponseAvroModel.getPaymentId(), paymentResponse.getPaymentId());
        assertEquals(paymentResponseAvroModel.getCustomerId(), paymentResponse.getCustomerId());
        assertEquals(paymentResponseAvroModel.getOrderId(), paymentResponse.getOrderId());
        assertEquals(paymentResponseAvroModel.getPrice(), paymentResponse.getPrice());
        assertEquals(paymentResponseAvroModel.getCreatedAt(), paymentResponse.getCreatedAt());
        assertEquals(paymentResponseAvroModel.getPaymentStatus().toString(), paymentResponse.getPaymentStatus().toString());
        assertThat(paymentResponse.getFailureMessages()).containsAll(paymentResponseAvroModel.getFailureMessages());
    }

    @Test
    void restaurantApprovalResponseAvroModel_restaurantApprovalResponseAvroModelToRestaurantApprovalResponse_restaurantApprovalResponse() {
        com.food.ordering.kafka.order.model.RestaurantApprovalResponse restaurantApprovalResponseAvroModel =
                com.food.ordering.kafka.order.model.RestaurantApprovalResponse.newBuilder()
                        .setId(TEST_RESTAURANT_APPROVAL_RESPONSE_EVENT_ID.toString())
                        .setSagaId("")
                        .setRestaurantId(TEST_RESTAURANT_ID.toString())
                        .setOrderId(TEST_ORDER_ID.toString())
                        .setCreatedAt(TEST_ZONE_DATE_TIME.toInstant())
                        .setOrderApprovalStatus(OrderApprovalStatus.REJECTED)
                        .setFailureMessages(List.of("fail1", "fail2", "fail3"))
                        .build();

        RestaurantApprovalResponse restaurantApprovalResponse =
                orderMessagingMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(restaurantApprovalResponseAvroModel);

        assertEquals(restaurantApprovalResponseAvroModel.getId(), restaurantApprovalResponse.getId());
        assertEquals(restaurantApprovalResponseAvroModel.getSagaId(), restaurantApprovalResponse.getSagaId());
        assertEquals(restaurantApprovalResponseAvroModel.getRestaurantId(), restaurantApprovalResponse.getRestaurantId());
        assertEquals(restaurantApprovalResponseAvroModel.getOrderId(), restaurantApprovalResponse.getOrderId());
        assertEquals(restaurantApprovalResponseAvroModel.getCreatedAt(), restaurantApprovalResponse.getCreatedAt());
        assertEquals(restaurantApprovalResponseAvroModel.getOrderApprovalStatus().toString(), restaurantApprovalResponse.getOrderApprovalStatus().toString());
        assertThat(restaurantApprovalResponse.getFailureMessages()).containsAll(restaurantApprovalResponseAvroModel.getFailureMessages());
    }

    @Test
    void orderPaymentEventPayload_orderPaymentEventPayloadToPaymentRequestAvroModel_paymentRequestAvroModel() {
        OrderPaymentEventPayload orderPaymentEventPayload = OrderPaymentEventPayload.builder()
                .orderId(TEST_ORDER_ID.getValue().toString())
                .customerId(TEST_CUSTOMER_ID.getValue().toString())
                .price(TEST_PRICE.getAmount())
                .createdAt(TEST_ZONE_DATE_TIME)
                .paymentOrderStatus("PENDING")
                .build();

        PaymentRequest paymentRequest = orderMessagingMapper.orderPaymentEventPayloadToPaymentRequestAvroModel(TEST_SAGA_ID.toString(),
                orderPaymentEventPayload);

        assertNotNull(paymentRequest.getId());
        assertEquals(TEST_SAGA_ID.toString(), paymentRequest.getSagaId());
        assertEquals(orderPaymentEventPayload.getOrderId(), paymentRequest.getOrderId());
        assertEquals(orderPaymentEventPayload.getCustomerId(), paymentRequest.getCustomerId());
        assertEquals(orderPaymentEventPayload.getPrice(), paymentRequest.getPrice());
        assertEquals(orderPaymentEventPayload.getPaymentOrderStatus(), paymentRequest.getPaymentOrderStatus().toString());
        assertEquals(orderPaymentEventPayload.getCreatedAt(), ZonedDateTime.ofInstant(paymentRequest.getCreatedAt(), UTC));
    }

    @Test
    void orderApprovalEventPayload_orderApprovalEventPayloadToRestaurantApprovalRequestAvroModel_restaurantApprovalRequestAvroModel() {
        OrderApprovalEventPayload orderApprovalEventPayload = OrderApprovalEventPayload.builder()
                .orderId(TEST_ORDER_ID.getValue().toString())
                .restaurantId(TEST_RESTAURANT_ID.getValue().toString())
                .price(TEST_PRICE.getAmount())
                .createdAt(TEST_ZONE_DATE_TIME)
                .restaurantOrderStatus("PAID")
                .products(createOrderApprovalEventProduct())
                .build();

        RestaurantApprovalRequest restaurantApprovalRequest =
                orderMessagingMapper.orderApprovalEventPayloadToRestaurantApprovalRequestAvroModel(TEST_SAGA_ID.toString(),
                        orderApprovalEventPayload);

        assertNotNull(restaurantApprovalRequest.getId());
        assertEquals(TEST_SAGA_ID.toString(), restaurantApprovalRequest.getSagaId());
        assertEquals(orderApprovalEventPayload.getOrderId(), restaurantApprovalRequest.getOrderId());
        assertEquals(orderApprovalEventPayload.getRestaurantId(), restaurantApprovalRequest.getRestaurantId());
        assertEquals(orderApprovalEventPayload.getRestaurantOrderStatus(), restaurantApprovalRequest.getRestaurantOrderStatus().toString());

        assertEquals(2, restaurantApprovalRequest.getProducts().size());
        assertEquals(orderApprovalEventPayload.getProducts().get(0).getId(), restaurantApprovalRequest.getProducts().get(0).getId());
        assertEquals(orderApprovalEventPayload.getProducts().get(0).getQuantity(), restaurantApprovalRequest.getProducts().get(0).getQuantity());
        assertEquals(orderApprovalEventPayload.getProducts().get(1).getId(), restaurantApprovalRequest.getProducts().get(1).getId());
        assertEquals(orderApprovalEventPayload.getProducts().get(1).getQuantity(), restaurantApprovalRequest.getProducts().get(1).getQuantity());

        assertEquals(orderApprovalEventPayload.getPrice(), restaurantApprovalRequest.getPrice());
        assertEquals(orderApprovalEventPayload.getCreatedAt(), ZonedDateTime.ofInstant(restaurantApprovalRequest.getCreatedAt(), UTC));
    }

    private List<OrderApprovalEventProduct> createOrderApprovalEventProduct() {
        return List.of(OrderApprovalEventProduct.builder().id(TEST_PRODUCT_ID_1.getValue().toString()).quantity(2).build(),
                OrderApprovalEventProduct.builder().id(TEST_PRODUCT_ID_2.getValue().toString()).quantity(5).build());
    }
}