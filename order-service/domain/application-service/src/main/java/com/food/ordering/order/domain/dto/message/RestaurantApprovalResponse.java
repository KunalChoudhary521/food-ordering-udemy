package com.food.ordering.order.domain.dto.message;

import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class RestaurantApprovalResponse {

    private final String id;
    private final String sagaId;
    private final String orderId;
    private final String restaurantId;
    private final Instant createdAt;
    private final OrderApprovalStatus orderApprovalStatus;
    private final List<String> failureMessages;
}
