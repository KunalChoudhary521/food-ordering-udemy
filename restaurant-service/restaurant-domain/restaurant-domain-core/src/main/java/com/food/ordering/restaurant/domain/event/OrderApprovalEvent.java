package com.food.ordering.restaurant.domain.event;

import com.food.ordering.domain.event.DomainEvent;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.domain.entity.OrderApproval;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {

    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;
}
