package com.food.ordering.order.domain.mapper;

import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.create.OrderAddress;
import com.food.ordering.order.domain.dto.create.OrderItemDto;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.valueobject.TrackingId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class OrderMapperTest {

    private static final UUID TEST_ORDER_ID = UUID.fromString("c6f32d19-4547-4431-ba1e-5bb81fc94e06");
    private static final UUID TEST_CUSTOMER_ID = UUID.fromString("bc6ddfe0-38c1-4af6-8c0f-4b29a0085217");
    private static final UUID TEST_RESTAURANT_ID = UUID.fromString("45c4081b-e5e8-4d6e-a896-7038837360df");
    private static final UUID TEST_PRODUCT_ID = UUID.fromString("daaee495-8a75-43a8-bfff-ec5e5e35db00");
    public static final Money ORDER_PRICE = new Money(new BigDecimal("20.83"));

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
        assertEquals(TEST_CUSTOMER_ID, order.getCustomerId().getValue());
        assertEquals(TEST_RESTAURANT_ID, order.getRestaurantId().getValue());

        assertNotNull(order.getDeliveryAddress().getId());
        assertEquals(orderAddress.getStreet(), order.getDeliveryAddress().getStreet());
        assertEquals(orderAddress.getCity(), order.getDeliveryAddress().getCity());
        assertEquals(orderAddress.getCountry(), order.getDeliveryAddress().getCountry());
        assertEquals(orderAddress.getPostalCode(), order.getDeliveryAddress().getPostalCode());
        assertEquals(createOrderCommand.getPrice(), order.getPrice().getAmount());

        assertEquals(1, order.getOrderItems().size());

        OrderItem orderItemDto = order.getOrderItems().get(0);
        assertEquals(TEST_PRODUCT_ID, orderItemDto.getProduct().getId().getValue());
        assertEquals(ORDER_PRICE, orderItemDto.getPrice());
        assertNull(orderItemDto.getProduct().getPrice());
        assertNull(orderItemDto.getProduct().getName());

        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertNull(order.getFailureMessages());
        assertNull(order.getTrackingId());
    }

    @Test
    void order_orderToTrackOrderResponse_trackOrderResponse() {
        Order order = Order.builder()
                .trackingId(new TrackingId(TEST_ORDER_ID))
                .orderStatus(OrderStatus.APPROVED)
                .failureMessages(List.of("failure1", "failure2"))
                .build();

        TrackOrderResponse trackOrderResponse = orderMapper.orderToTrackOrderResponse(order);

        assertEquals(TEST_ORDER_ID, trackOrderResponse.getTrackingId());
        assertEquals(OrderStatus.APPROVED, trackOrderResponse.getOrderStatus());
        assertEquals(2, trackOrderResponse.getFailureMessages().size());
        assertThat(trackOrderResponse.getFailureMessages()).contains("failure2", "failure1");
    }

    @Test
    void order_orderToCreateOrderResponse_createOrderResponse() {
        String message = "test message";
        Order order = Order.builder()
                .trackingId(new TrackingId(TEST_ORDER_ID))
                .orderStatus(OrderStatus.PAID)
                .build();

        CreateOrderResponse createOrderResponse = orderMapper.orderToCreateOrderResponse(order, message);

        assertEquals(TEST_ORDER_ID, createOrderResponse.getTrackingId());
        assertEquals(OrderStatus.PAID, createOrderResponse.getOrderStatus());
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
        assertEquals(TEST_PRODUCT_ID, product.getId().getValue());
        assertNull(product.getName());
        assertEquals(ORDER_PRICE, product.getPrice());
    }

    private static CreateOrderCommand getCreateOrderCommand(OrderAddress orderAddress) {
        List<OrderItemDto> orderItemDtos = List.of(
                OrderItemDto.builder()
                        .price(ORDER_PRICE.getAmount())
                        .productId(TEST_PRODUCT_ID)
                        .quantity(1)
                        .subTotal(ORDER_PRICE.getAmount())
                        .build());

        return CreateOrderCommand.builder()
                .customerId(TEST_CUSTOMER_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .price(ORDER_PRICE.getAmount())
                .orderItems(orderItemDtos)
                .orderAddress(orderAddress)
                .build();
    }
}