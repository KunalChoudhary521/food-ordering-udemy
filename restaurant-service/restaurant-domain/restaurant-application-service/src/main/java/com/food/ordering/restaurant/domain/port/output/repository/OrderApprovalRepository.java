package com.food.ordering.restaurant.domain.port.output.repository;

import com.food.ordering.restaurant.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
