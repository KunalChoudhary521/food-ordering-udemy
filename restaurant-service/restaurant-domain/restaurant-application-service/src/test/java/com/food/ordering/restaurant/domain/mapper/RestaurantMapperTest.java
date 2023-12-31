package com.food.ordering.restaurant.domain.mapper;

import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.restaurant.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.restaurant.domain.entity.OrderApproval;
import com.food.ordering.restaurant.domain.entity.Product;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderRejectedEvent;
import com.food.ordering.restaurant.domain.outbox.model.OrderEventPayload;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RestaurantMapperTest {

    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("bfd20746-6721-4beb-831b-c1957355c8c3"));
    private static final RestaurantId TEST_RESTAURANT_ID = new RestaurantId(UUID.fromString("224ee6e2-ea1e-4058-9887-7dae9e93ca97"));
    private static final Money ORDER_PRICE = new Money(new BigDecimal("16.09"));
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 12, 12, 6, 5, 21, 0, ZoneOffset.UTC);
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("83b03dbf-c7d4-40dc-8df7-05ef32caa319"));
    private static final ProductId TEST_PRODUCT_ID_2 = new ProductId(UUID.fromString("53347a3d-7a1c-4292-a417-9ab36cfa5f65"));

    private final RestaurantMapper restaurantMapper = Mappers.getMapper(RestaurantMapper.class);

    @Test
    void restaurantApprovalRequest_restaurantApprovalRequestToRestaurant_restaurant() {
        List<Product> products = List.of(
                new Product(TEST_PRODUCT_ID_1, "p1", new Money(new BigDecimal("4.49")), 2, true),
                new Product(TEST_PRODUCT_ID_2, "p2", new Money(new BigDecimal("8.99")), 1, true));

        RestaurantApprovalRequest restaurantApprovalRequest = RestaurantApprovalRequest.builder()
                .restaurantId(TEST_RESTAURANT_ID.getValue().toString())
                .orderId(TEST_ORDER_ID.getValue().toString())
                .price(ORDER_PRICE.getAmount())
                .restaurantOrderStatus(RestaurantOrderStatus.PAID)
                .createdAt(TEST_ZONE_DATE_TIME.toInstant())
                .products(products)
                .build();

        Restaurant restaurant = restaurantMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);

        assertEquals(restaurantApprovalRequest.getRestaurantId(), restaurant.getId().getValue().toString());

        assertNull(restaurant.getOrderApproval());

        assertEquals(restaurantApprovalRequest.getOrderId(), restaurant.getOrderDetail().getId().getValue().toString());
        assertEquals(2, restaurant.getOrderDetail().getProducts().size());

        assertEquals(restaurantApprovalRequest.getProducts().get(0).getId(), restaurant.getOrderDetail().getProducts().get(0).getId());
        assertEquals(restaurantApprovalRequest.getProducts().get(0).getQuantity(), restaurant.getOrderDetail().getProducts().get(0).getQuantity());
        assertEquals(restaurantApprovalRequest.getProducts().get(0).getName(), restaurant.getOrderDetail().getProducts().get(0).getName());
        assertEquals(restaurantApprovalRequest.getProducts().get(0).getPrice(), restaurant.getOrderDetail().getProducts().get(0).getPrice());

        assertEquals(restaurantApprovalRequest.getPrice(), restaurant.getOrderDetail().getTotalAmount().getAmount());
        assertEquals(restaurantApprovalRequest.getRestaurantOrderStatus().toString(), restaurant.getOrderDetail().getOrderStatus().toString());
    }

    @Test
    void orderApprovalEvent_orderApprovalEventToOrderEventPayload_orderEventPayload() {
        OrderApproval orderApproval = OrderApproval.builder()
                .orderId(TEST_ORDER_ID)
                .approvalStatus(OrderApprovalStatus.REJECTED)
                .build();
        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(orderApproval, TEST_RESTAURANT_ID,
                List.of("fail1", "fail3", "fail2"), TEST_ZONE_DATE_TIME);

        OrderEventPayload orderEventPayload = restaurantMapper.orderApprovalEventToOrderEventPayload(orderRejectedEvent);

        assertEquals(orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString(), orderEventPayload.getOrderId());
        assertEquals(orderRejectedEvent.getRestaurantId().getValue().toString(), orderEventPayload.getRestaurantId());
        assertEquals(orderRejectedEvent.getCreatedAt(), orderEventPayload.getCreatedAt());
        assertEquals(orderRejectedEvent.getOrderApproval().getApprovalStatus().toString(), orderEventPayload.getOrderApprovalStatus());
        assertThat(orderEventPayload.getFailureMessages()).containsAll(orderRejectedEvent.getFailureMessages());

    }
}