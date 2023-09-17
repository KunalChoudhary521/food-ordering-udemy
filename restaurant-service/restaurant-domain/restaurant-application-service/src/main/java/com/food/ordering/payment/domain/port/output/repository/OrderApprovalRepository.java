package com.food.ordering.payment.domain.port.output.repository;

import com.food.ordering.restaurant.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
