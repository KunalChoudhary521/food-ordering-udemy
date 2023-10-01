package com.food.ordering.order.domain.outbox.model.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderApprovalEventProduct {

    private String id;
    private Integer quantity;
}
