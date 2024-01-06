package com.food.ordering.order.domain.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.PaymentOrderStatus;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.create.OrderAddress;
import com.food.ordering.order.domain.dto.create.OrderItemDto;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.order.domain.valueobject.TrackingId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OrderMapperTest {

    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("c6f32d19-4547-4431-ba1e-5bb81fc94e06"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("bc6ddfe0-38c1-4af6-8c0f-4b29a0085217"));
    private static final RestaurantId TEST_RESTAURANT_ID = new RestaurantId(UUID.fromString("45c4081b-e5e8-4d6e-a896-7038837360df"));
    private static final TrackingId TEST_TRACKING_ID = new TrackingId(UUID.fromString("dcd086bc-9012-4371-a6ee-132325faafde"));
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("daaee495-8a75-43a8-bfff-ec5e5e35db00"));
    private static final ProductId TEST_PRODUCT_ID_2 = new ProductId(UUID.fromString("289d16a9-a43b-4c4f-aa58-5324357b9d23"));
    private static final Money ORDER_PRICE = new Money(new BigDecimal("20.83"));
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 12, 12, 6, 5, 21, 0, UTC);

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void createOrderCommand_createOrderCommandToOrder_order() {
        OrderAddress orderAddress = OrderAddress.builder()
                .street("123 Test Street Rd.")
                .city("Test city")
                .country("Test country")
                .postalCode("12345")
                .build();
        CreateOrderCommand createOrderCommand = getCreateOrderCommand(orderAddress);

        Order order = orderMapper.createOrderCommandToOrder(createOrderCommand);

        assertNotNull(order);
        assertNull(order.getId());
        assertEquals(TEST_CUSTOMER_ID.getValue(), order.getCustomerId().getValue());
        assertEquals(TEST_RESTAURANT_ID.getValue(), order.getRestaurantId().getValue());

        assertNotNull(order.getDeliveryAddress().getId());
        assertEquals(orderAddress.getStreet(), order.getDeliveryAddress().getStreet());
        assertEquals(orderAddress.getCity(), order.getDeliveryAddress().getCity());
        assertEquals(orderAddress.getCountry(), order.getDeliveryAddress().getCountry());
        assertEquals(orderAddress.getPostalCode(), order.getDeliveryAddress().getPostalCode());
        assertEquals(createOrderCommand.getPrice(), order.getPrice().getAmount());

        assertEquals(1, order.getOrderItems().size());

        OrderItem orderItemDto = order.getOrderItems().get(0);
        assertEquals(TEST_PRODUCT_ID_1.getValue(), orderItemDto.getProduct().getId().getValue());
        assertEquals(ORDER_PRICE, orderItemDto.getPrice());
        assertEquals(ORDER_PRICE, orderItemDto.getProduct().getPrice());
        assertNull(orderItemDto.getProduct().getName());

        assertNull(order.getOrderStatus());
        assertTrue(order.getFailureMessages().isEmpty());
        assertNull(order.getTrackingId());
    }

    @Test
    void order_orderToTrackOrderResponse_trackOrderResponse() {
        Order order = Order.builder()
                .trackingId(TEST_TRACKING_ID)
                .orderStatus(OrderStatus.APPROVED)
                .failureMessages(List.of("failure1", "failure2"))
                .build();

        TrackOrderResponse trackOrderResponse = orderMapper.orderToTrackOrderResponse(order);

        assertEquals(order.getTrackingId().getValue(), trackOrderResponse.getTrackingId());
        assertEquals(order.getOrderStatus(), trackOrderResponse.getOrderStatus());
        assertEquals(2, trackOrderResponse.getFailureMessages().size());
        assertThat(trackOrderResponse.getFailureMessages()).contains("failure2", "failure1");
    }

    @Test
    void order_orderToCreateOrderResponse_createOrderResponse() {
        String message = "test message";
        Order order = Order.builder()
                .trackingId(TEST_TRACKING_ID)
                .orderStatus(OrderStatus.PAID)
                .build();

        CreateOrderResponse createOrderResponse = orderMapper.orderToCreateOrderResponse(order, message);

        assertEquals(order.getTrackingId().getValue(), createOrderResponse.getTrackingId());
        assertEquals(order.getOrderStatus(), createOrderResponse.getOrderStatus());
        assertEquals(message, createOrderResponse.getMessage());
    }

    @Test
    public void createOrderCommand_createOrderCommandToRestaurant_restaurant() {
        CreateOrderCommand createOrderCommand = getCreateOrderCommand(OrderAddress.builder().build());

        Restaurant restaurant = orderMapper.createOrderCommandToRestaurant(createOrderCommand);

        assertEquals(createOrderCommand.getRestaurantId(), restaurant.getId().getValue());
        assertNull(restaurant.getName());
        assertFalse(restaurant.isActive());
        assertEquals(1, restaurant.getProducts().size());

        Product product = restaurant.getProducts().get(0);
        assertEquals(TEST_PRODUCT_ID_1.getValue(), product.getId().getValue());
        assertNull(product.getName());
        assertEquals(ORDER_PRICE, product.getPrice());
    }

    @Test
    public void orderCreatedEvent_orderCreatedEventToOrderPaymentEventPayload_orderPaymentEventPayload() {
        Order order = Order.builder()
                .id(TEST_ORDER_ID)
                .customerId(TEST_CUSTOMER_ID)
                .price(ORDER_PRICE)
                .build();
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(order, TEST_ZONE_DATE_TIME);

        OrderPaymentEventPayload orderPaymentEventPayload = orderMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent);

        assertEquals(orderCreatedEvent.getOrder().getId().getValue().toString(), orderPaymentEventPayload.getOrderId());
        assertEquals(orderCreatedEvent.getOrder().getCustomerId().getValue().toString(), orderPaymentEventPayload.getCustomerId());
        assertEquals(orderCreatedEvent.getOrder().getPrice().getAmount(), orderPaymentEventPayload.getPrice());
        assertEquals(orderCreatedEvent.getCreatedAt(), orderPaymentEventPayload.getCreatedAt());
        assertEquals(PaymentOrderStatus.PENDING.toString(), orderPaymentEventPayload.getPaymentOrderStatus());
    }

    @Test
    public void orderPaidEvent_orderPaidEventToOrderApprovalEventPayload_orderApprovalEventPayload() {
        Order order = Order.builder()
                .id(TEST_ORDER_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .orderItems(getOrderItems())
                .price(ORDER_PRICE)
                .build();
        OrderPaidEvent orderPaidEvent = new OrderPaidEvent(order, TEST_ZONE_DATE_TIME);

        OrderApprovalEventPayload orderApprovalEventPayload = orderMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent);

        assertEquals(orderPaidEvent.getOrder().getId().getValue().toString(), orderApprovalEventPayload.getOrderId());
        assertEquals(orderPaidEvent.getOrder().getRestaurantId().getValue().toString(), orderApprovalEventPayload.getRestaurantId());
        assertEquals(RestaurantOrderStatus.PAID.name(), orderApprovalEventPayload.getRestaurantOrderStatus());

        assertEquals(2, orderApprovalEventPayload.getProducts().size());
        assertEquals(orderPaidEvent.getOrder().getOrderItems().get(0).getProduct().getId().getValue().toString(), orderApprovalEventPayload.getProducts().get(0).getId());
        assertEquals(orderPaidEvent.getOrder().getOrderItems().get(0).getQuantity(), orderApprovalEventPayload.getProducts().get(0).getQuantity());

        assertEquals(orderPaidEvent.getOrder().getOrderItems().get(1).getProduct().getId().getValue().toString(), orderApprovalEventPayload.getProducts().get(1).getId());
        assertEquals(orderPaidEvent.getOrder().getOrderItems().get(1).getQuantity(), orderApprovalEventPayload.getProducts().get(1).getQuantity());

        assertEquals(orderPaidEvent.getOrder().getPrice().getAmount(), orderApprovalEventPayload.getPrice());
        assertEquals(orderPaidEvent.getCreatedAt(), orderApprovalEventPayload.getCreatedAt());
    }

    @Test
    public void orderCancelledEvent_orderCancelledEventToOrderPaymentEventPayload_orderPaymentEventPayload() {
        Order order = Order.builder()
                .id(TEST_ORDER_ID)
                .customerId(TEST_CUSTOMER_ID)
                .price(ORDER_PRICE)
                .build();

        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(order, TEST_ZONE_DATE_TIME);

        OrderPaymentEventPayload orderPaymentEventPayload = orderMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledEvent);

        assertEquals(orderCancelledEvent.getOrder().getId().getValue().toString(), orderPaymentEventPayload.getOrderId());
        assertEquals(orderCancelledEvent.getOrder().getCustomerId().getValue().toString(), orderPaymentEventPayload.getCustomerId());
        assertEquals(orderCancelledEvent.getOrder().getPrice().getAmount(), orderPaymentEventPayload.getPrice());
        assertEquals(PaymentOrderStatus.CANCELLED.name(), orderPaymentEventPayload.getPaymentOrderStatus());
        assertEquals(orderCancelledEvent.getCreatedAt(), orderPaymentEventPayload.getCreatedAt());
    }

    private static CreateOrderCommand getCreateOrderCommand(OrderAddress orderAddress) {
        List<OrderItemDto> orderItemDtos = List.of(
                OrderItemDto.builder()
                        .price(ORDER_PRICE.getAmount())
                        .productId(TEST_PRODUCT_ID_1.getValue())
                        .quantity(1)
                        .subTotal(ORDER_PRICE.getAmount())
                        .build());

        return CreateOrderCommand.builder()
                .customerId(TEST_CUSTOMER_ID.getValue())
                .restaurantId(TEST_RESTAURANT_ID.getValue())
                .price(ORDER_PRICE.getAmount())
                .orderItems(orderItemDtos)
                .orderAddress(orderAddress)
                .build();
    }

    private List<OrderItem> getOrderItems() {
        OrderItem item1 = OrderItem.builder()
                .product(new Product(TEST_PRODUCT_ID_1, "p1", ORDER_PRICE))
                .quantity(2)
                .build();

        OrderItem item2 = OrderItem.builder()
                .product(new Product(TEST_PRODUCT_ID_2, "p2", ORDER_PRICE))
                .quantity(3)
                .build();

        return List.of(item1, item2);
    }

}