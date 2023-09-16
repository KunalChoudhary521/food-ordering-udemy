package com.food.ordering.restaurant.domain;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderApprovalEvent;
import com.food.ordering.restaurant.domain.event.OrderApprovedEvent;
import com.food.ordering.restaurant.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages,
                                     DomainEventPublisher<OrderApprovedEvent> orderApprovedDomainEventPublisher,
                                     DomainEventPublisher<OrderRejectedEvent> orderRejectedDomainEventPublisher);
}
