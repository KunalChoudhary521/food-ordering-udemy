package com.food.ordering.restaurant.domain.port.input.listener;

import com.food.ordering.restaurant.domain.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestListener {

    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
