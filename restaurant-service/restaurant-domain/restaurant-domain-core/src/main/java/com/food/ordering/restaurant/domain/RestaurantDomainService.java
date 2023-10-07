package com.food.ordering.restaurant.domain;

import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages);
}
