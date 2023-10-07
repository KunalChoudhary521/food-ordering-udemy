package com.food.ordering.restaurant.domain.outbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderEventPayload {

    private String orderId;
    private String restaurantId;
    private ZonedDateTime createdAt;
    private String orderApprovalStatus;
    private List<String> failureMessages;
}
