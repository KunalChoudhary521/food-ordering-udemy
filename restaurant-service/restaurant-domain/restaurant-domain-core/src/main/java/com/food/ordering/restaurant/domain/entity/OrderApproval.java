package com.food.ordering.restaurant.domain.entity;

import com.food.ordering.domain.entity.BaseEntity;
import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.domain.valueobject.OrderApprovalId;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderApproval extends BaseEntity<OrderApprovalId> {

    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalStatus approvalStatus;

    @Builder
    public OrderApproval(OrderApprovalId orderApprovalId, RestaurantId restaurantId, OrderId orderId, OrderApprovalStatus approvalStatus) {
        super.setId(orderApprovalId);
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.approvalStatus = approvalStatus;
    }
}
