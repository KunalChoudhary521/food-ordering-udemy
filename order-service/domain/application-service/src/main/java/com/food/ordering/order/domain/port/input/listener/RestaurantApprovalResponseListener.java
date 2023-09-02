package com.food.ordering.order.domain.port.input.listener;

import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);
    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
