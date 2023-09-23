package com.food.ordering.restaurant.messaging.mapper;

import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.kafka.order.model.Product;
import com.food.ordering.kafka.order.model.RestaurantOrderStatus;
import com.food.ordering.payment.domain.dto.RestaurantApprovalRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantMessagingMapperTest {

    private static final UUID TEST_RESTAURANT_APPROVAL_REQUEST_EVENT_ID = UUID.fromString("9908772e-0ba4-4e93-b2a4-5753cbe98ef1");
    private static final RestaurantId TEST_RESTAURANT_ID = new RestaurantId(UUID.fromString("d07a7385-921e-4e79-afd3-fcbe29a5aed4"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("9a62bcae-9708-43bf-a19f-9e514221110a"));
    public static final Money TEST_PRICE = new Money(new BigDecimal("11.07"));
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZonedDateTime TEST_ZONE_DATE_TIME = ZonedDateTime.of(2023, 9, 23, 9, 17, 21, 0, UTC);
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("c4b9a4a2-a36e-478a-b46b-3f891e56d502"));
    private static final ProductId TEST_PRODUCT_ID_2 = new ProductId(UUID.fromString("e1830351-b1d8-47b7-b712-8693fd4bdc26"));

    private final RestaurantMessagingMapper restaurantMessagingMapper = Mappers.getMapper(RestaurantMessagingMapper.class);

    @Test
    void restaurantApprovalRequest_restaurantApprovalRequestAvroModelToRestaurantApprovalRequest_restaurantApprovalRequest() {
        List<Product> products = List.of(
                Product.newBuilder().setId(TEST_PRODUCT_ID_1.getValue().toString()).setQuantity(2).build(),
                Product.newBuilder().setId(TEST_PRODUCT_ID_2.getValue().toString()).setQuantity(3).build());

        com.food.ordering.kafka.order.model.RestaurantApprovalRequest avroModel =
                com.food.ordering.kafka.order.model.RestaurantApprovalRequest.newBuilder()
                        .setId(TEST_RESTAURANT_APPROVAL_REQUEST_EVENT_ID.toString())
                        .setSagaId("")
                        .setRestaurantId(TEST_RESTAURANT_ID.getValue().toString())
                        .setOrderId(TEST_ORDER_ID.getValue().toString())
                        .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                        .setProducts(products)
                        .setPrice(TEST_PRICE.getAmount())
                        .setCreatedAt(TEST_ZONE_DATE_TIME.toInstant())
                        .build();

        RestaurantApprovalRequest restaurantApprovalRequest = restaurantMessagingMapper.restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(avroModel);

        assertEquals(avroModel.getId(), restaurantApprovalRequest.getId());
        assertEquals(avroModel.getSagaId(), restaurantApprovalRequest.getSagaId());
        assertEquals(avroModel.getRestaurantId(), restaurantApprovalRequest.getRestaurantId());
        assertEquals(avroModel.getOrderId(), restaurantApprovalRequest.getOrderId());
        assertEquals(avroModel.getRestaurantOrderStatus().toString(), restaurantApprovalRequest.getRestaurantOrderStatus().toString());

        assertEquals(avroModel.getProducts().size(), restaurantApprovalRequest.getProducts().size());
        assertEquals(avroModel.getProducts().get(0).getId(), restaurantApprovalRequest.getProducts().get(0).getId().getValue().toString());
        assertEquals(avroModel.getProducts().get(0).getQuantity(), restaurantApprovalRequest.getProducts().get(0).getQuantity());

        assertEquals(avroModel.getProducts().get(1).getId(), restaurantApprovalRequest.getProducts().get(1).getId().getValue().toString());
        assertEquals(avroModel.getProducts().get(1).getQuantity(), restaurantApprovalRequest.getProducts().get(1).getQuantity());

        assertEquals(avroModel.getPrice(), restaurantApprovalRequest.getPrice());
        assertEquals(avroModel.getCreatedAt(), restaurantApprovalRequest.getCreatedAt());
    }
}