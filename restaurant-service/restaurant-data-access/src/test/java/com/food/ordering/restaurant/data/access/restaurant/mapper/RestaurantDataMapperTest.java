package com.food.ordering.restaurant.data.access.restaurant.mapper;

import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.data.access.restaurant.entity.OrderApprovalEntity;
import com.food.ordering.restaurant.domain.entity.OrderApproval;
import com.food.ordering.restaurant.domain.valueobject.OrderApprovalId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantDataMapperTest {

    private static final OrderApprovalId TEST_ORDER_APPROVAL_ID = new OrderApprovalId(UUID.fromString("6cedfd08-0758-414f-9867-ba5af91b788c"));
    private static final OrderId TEST_ORDER_ID = new OrderId(UUID.fromString("7fec6647-2e14-4fa9-be47-636039206682"));
    private static final RestaurantId TEST_RESTAURANT_ID = new RestaurantId(UUID.fromString("652b7a9d-1e44-4b2b-80c3-b6144eb05192"));

    private final RestaurantDataMapper restaurantDataMapper = Mappers.getMapper(RestaurantDataMapper.class);

    @Test
    void orderApproval_orderApprovalToOrderApprovalEntity_orderApprovalEntity() {
        OrderApproval orderApproval = OrderApproval.builder()
                .orderApprovalId(TEST_ORDER_APPROVAL_ID)
                .orderId(TEST_ORDER_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .approvalStatus(OrderApprovalStatus.APPROVED)
                .build();

        OrderApprovalEntity orderApprovalEntity = restaurantDataMapper.orderApprovalToOrderApprovalEntity(orderApproval);

        assertEquals(orderApproval.getId().getValue(), orderApprovalEntity.getId());
        assertEquals(orderApproval.getRestaurantId().getValue(), orderApprovalEntity.getRestaurantId());
        assertEquals(orderApproval.getOrderId().getValue(), orderApprovalEntity.getOrderId());
        assertEquals(orderApproval.getApprovalStatus(), orderApprovalEntity.getStatus());
    }

    @Test
    void orderApprovalEntity_orderApprovalEntityToOrderApproval_orderApproval() {
        OrderApprovalEntity orderApprovalEntity = OrderApprovalEntity.builder()
                .id(TEST_ORDER_APPROVAL_ID.getValue())
                .orderId(TEST_ORDER_ID.getValue())
                .restaurantId(TEST_RESTAURANT_ID.getValue())
                .status(OrderApprovalStatus.REJECTED)
                .build();

        OrderApproval orderApproval = restaurantDataMapper.orderApprovalEntityToOrderApproval(orderApprovalEntity);

        assertEquals(orderApprovalEntity.getId(), orderApproval.getId().getValue());
        assertEquals(orderApprovalEntity.getRestaurantId(), orderApproval.getRestaurantId().getValue());
        assertEquals(orderApprovalEntity.getOrderId(), orderApproval.getOrderId().getValue());
        assertEquals(orderApprovalEntity.getStatus(), orderApproval.getApprovalStatus());
    }
}