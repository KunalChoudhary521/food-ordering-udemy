package com.food.ordering.order.domain.outbox.model.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderApprovalEventPayload {
    private String orderId;
    private String restaurantId;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private String restaurantOrderStatus;
    private List<OrderApprovalEventProduct> products;
}
