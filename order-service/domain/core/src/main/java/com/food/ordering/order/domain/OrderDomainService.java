package com.food.ordering.order.domain;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitOrder(Order order, Restaurant restaurant,
                                           DomainEventPublisher<OrderCreatedEvent> orderCreatedDomainEventPublisher);

    OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidDomainEventPublisher);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages,
                                           DomainEventPublisher<OrderCancelledEvent> orderCancelledDomainEventPublisher);

    void cancelOrder(Order order, List<String> failureMessages);
}
