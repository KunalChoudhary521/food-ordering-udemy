package com.food.ordering.order.domain.mapper;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.OrderAddress;
import com.food.ordering.order.domain.dto.create.OrderItem;
import com.food.ordering.order.domain.entity.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class OrderDataMapperTest {

    private static final UUID TEST_CUSTOMER_ID = UUID.fromString("bc6ddfe0-38c1-4af6-8c0f-4b29a0085217");
    private static final UUID TEST_RESTAURANT_ID = UUID.fromString("45c4081b-e5e8-4d6e-a896-7038837360df");
    private final OrderDataMapper orderDataMapper = new OrderDataMapper();

    @Test
    void createOrderCommand_createOrderCommandToOrder_Order() {
        OrderAddress orderAddress = OrderAddress.builder()
                .street("123 Test Street Rd.")
                .city("Test city")
                .country("Test country")
                .postalCode("12345")
                .build();
        BigDecimal amount = new BigDecimal("20.833");
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .price(amount)
                        .productId(UUID.randomUUID())
                        .quantity(1)
                        .subTotal(amount)
                        .build());
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerId(TEST_CUSTOMER_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .price(amount)
                .orderItems(orderItems)
                .orderAddress(orderAddress)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);

        assertNotNull(order);
        assertNull(order.getId());
        assertEquals(TEST_CUSTOMER_ID, order.getCustomerId().getValue());
        assertEquals(TEST_RESTAURANT_ID, order.getRestaurantId().getValue());

        assertNotNull(order.getDeliveryAddress().getId());
        assertEquals(orderAddress.getStreet(), order.getDeliveryAddress().getStreet());
        assertEquals(orderAddress.getCity(), order.getDeliveryAddress().getCity());
        assertEquals(orderAddress.getCountry(), order.getDeliveryAddress().getCountry());
        assertEquals(orderAddress.getPostalCode(), order.getDeliveryAddress().getPostalCode());

        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertNull(order.getFailureMessages());
        assertNull(order.getTrackingId());
    }
}