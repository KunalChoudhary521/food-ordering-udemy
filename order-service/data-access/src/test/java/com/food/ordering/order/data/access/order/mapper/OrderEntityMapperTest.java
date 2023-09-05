package com.food.ordering.order.data.access.order.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.order.data.access.order.entity.OrderAddressEntity;
import com.food.ordering.order.data.access.order.entity.OrderEntity;
import com.food.ordering.order.data.access.order.entity.OrderItemEntity;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.valueobject.OrderItemId;
import com.food.ordering.order.domain.valueobject.StreetAddress;
import com.food.ordering.order.domain.valueobject.TrackingId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderEntityMapperTest {

    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("ac9ff3b1-5d70-4202-97a2-cb25030f9f3a"));
    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("badfa28d-aee9-4d47-8be4-10f6d79ac5a2"));
    private static final RestaurantId TEST_RESTAURANT_ID = new RestaurantId(UUID.fromString("2653e66a-c2fc-4500-a67a-1e49143eddd6"));
    private static final TrackingId TEST_TRACKING_ID = new TrackingId(UUID.fromString("3e22c1d0-dfed-46ea-bd36-9f24b3c784be"));
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("efbb4a04-9127-412d-be66-756f933e0677"));
    private static final UUID TEST_ADDRESS_ID = UUID.fromString("ecba8e66-4776-4db3-9ba3-c25af16927a3");
    private static final OrderItemId TEST_ORDER_ITEM_ID = new OrderItemId(1L);
    public static final String TEST_STREET = "123 Test Street Rd.";
    public static final String TEST_CITY = "Test city";
    public static final String TEST_COUNTRY = "Test country";
    public static final String TEST_POSTAL_CODE = "12345";
    public static final BigDecimal ORDER_PRICE = new BigDecimal("43.67");

    private final OrderEntityMapper orderEntityMapper = Mappers.getMapper(OrderEntityMapper.class);

    @Test
    void orderEntity_orderEntityToOrder_order() {
        OrderAddressEntity orderAddressEntity = OrderAddressEntity.builder()
                .id(TEST_ADDRESS_ID)
                .street(TEST_STREET)
                .city(TEST_CITY)
                .country(TEST_COUNTRY)
                .postalCode(TEST_POSTAL_CODE)
                .build();
        List<OrderItemEntity> orderItems = List.of(
                OrderItemEntity.builder()
                        .id(TEST_ORDER_ITEM_ID.getValue())
                        .productId(TEST_PRODUCT_ID_1.getValue())
                        .quantity(2)
                        .price(new BigDecimal("21.83"))
                        .subTotal(ORDER_PRICE)
                        .build());
        OrderEntity orderEntity = OrderEntity.builder()
                .id(TEST_ORDER_ID.getValue())
                .customerId(TEST_CUSTOMER_ID.getValue())
                .restaurantId(TEST_RESTAURANT_ID.getValue())
                .trackingId(TEST_TRACKING_ID.getValue())
                .price(ORDER_PRICE)
                .orderStatus(OrderStatus.APPROVED)
                .failureMessages("fail1,fail2,fail3")
                .address(orderAddressEntity)
                .items(orderItems)
                .build();

        Order order = orderEntityMapper.orderEntityToOrder(orderEntity);

        assertEquals(orderEntity.getId(), order.getId().getValue());
        assertEquals(orderEntity.getCustomerId(), order.getCustomerId().getValue());
        assertEquals(orderEntity.getRestaurantId(), order.getRestaurantId().getValue());

        assertEquals(orderEntity.getAddress().getId(), order.getDeliveryAddress().getId());
        assertEquals(orderEntity.getAddress().getStreet(), order.getDeliveryAddress().getStreet());
        assertEquals(orderEntity.getAddress().getCity(), order.getDeliveryAddress().getCity());
        assertEquals(orderEntity.getAddress().getCountry(), order.getDeliveryAddress().getCountry());
        assertEquals(orderEntity.getAddress().getPostalCode(), order.getDeliveryAddress().getPostalCode());

        assertEquals(orderEntity.getPrice(), order.getPrice().getAmount());

        assertEquals(1, order.getOrderItems().size());
        OrderItem item = order.getOrderItems().get(0);
        assertEquals(orderEntity.getItems().get(0).getId(), item.getId().getValue());
        assertEquals(orderEntity.getItems().get(0).getProductId(), item.getProduct().getId().getValue());
        assertEquals(orderEntity.getItems().get(0).getQuantity(), item.getQuantity());
        assertEquals(orderEntity.getItems().get(0).getPrice(), item.getPrice().getAmount());
        assertEquals(orderEntity.getItems().get(0).getSubTotal(), item.getSubTotal().getAmount());

        assertEquals(orderEntity.getTrackingId(), order.getTrackingId().getValue());
        assertEquals(orderEntity.getOrderStatus(), order.getOrderStatus());
        assertThat(order.getFailureMessages()).contains("fail2", "fail3", "fail1");
    }

    @Test
    void order_orderToOrderEntity_orderEntity() {
        StreetAddress deliveryAddress = new StreetAddress(TEST_ADDRESS_ID, TEST_STREET,
                TEST_CITY, TEST_COUNTRY, TEST_POSTAL_CODE);
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .id(TEST_ORDER_ITEM_ID)
                        .price(new Money(ORDER_PRICE))
                        .quantity(1)
                        .subTotal(new Money(ORDER_PRICE))
                        .product(new Product(TEST_PRODUCT_ID_1, null, null))
                        .build());
        Order order = Order.builder()
                .id(TEST_ORDER_ID)
                .customerId(TEST_CUSTOMER_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .deliveryAddress(deliveryAddress)
                .price(new Money(ORDER_PRICE))
                .orderItems(orderItems)
                .trackingId(TEST_TRACKING_ID)
                .orderStatus(OrderStatus.CANCELLED)
                .failureMessages(List.of("test3", "test1", "test2"))
                .build();

        OrderEntity orderEntity = orderEntityMapper.orderToOrderEntity(order);

        assertEquals(order.getId().getValue(), orderEntity.getId());
        assertEquals(order.getCustomerId().getValue(), orderEntity.getCustomerId());
        assertEquals(order.getRestaurantId().getValue(), orderEntity.getRestaurantId());

        assertEquals(order.getDeliveryAddress().getId(), orderEntity.getAddress().getId());
        assertEquals(order.getDeliveryAddress().getStreet(), orderEntity.getAddress().getStreet());
        assertEquals(order.getDeliveryAddress().getCity(), orderEntity.getAddress().getCity());
        assertEquals(order.getDeliveryAddress().getCountry(), orderEntity.getAddress().getCountry());
        assertEquals(order.getDeliveryAddress().getPostalCode(), orderEntity.getAddress().getPostalCode());

        assertEquals(order.getPrice().getAmount(), orderEntity.getPrice());

        assertEquals(1, orderEntity.getItems().size());
        OrderItemEntity orderItemEntity = orderEntity.getItems().get(0);
        assertEquals(order.getOrderItems().get(0).getId().getValue(), orderItemEntity.getId());
        assertEquals(order.getOrderItems().get(0).getProduct().getId().getValue(), orderItemEntity.getProductId());
        assertEquals(order.getOrderItems().get(0).getQuantity(), orderItemEntity.getQuantity());
        assertEquals(order.getOrderItems().get(0).getPrice().getAmount(), orderItemEntity.getPrice());
        assertEquals(order.getOrderItems().get(0).getSubTotal().getAmount(), orderItemEntity.getSubTotal());

        assertEquals(orderEntity.getTrackingId(), order.getTrackingId().getValue());
        assertEquals(orderEntity.getOrderStatus(), order.getOrderStatus());
        assertEquals("test3,test1,test2", orderEntity.getFailureMessages());
    }
}