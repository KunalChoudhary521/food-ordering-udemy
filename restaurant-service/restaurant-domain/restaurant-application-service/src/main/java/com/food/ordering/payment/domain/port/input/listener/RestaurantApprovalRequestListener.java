package com.food.ordering.payment.domain.port.input.listener;

import com.food.ordering.payment.domain.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestListener {

    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
