package com.food.ordering.restaurant.domain.event;

import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

    public OrderRejectedEvent(OrderApproval orderApproval, RestaurantId restaurantId,
                              List<String> failureMessages, ZonedDateTime createdAt) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
    }
}
