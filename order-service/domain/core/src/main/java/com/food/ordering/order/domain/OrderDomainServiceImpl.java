package com.food.ordering.order.domain;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.order.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    private final static ZonedDateTime CURRENT_UTC_TIME = ZonedDateTime.now(ZoneId.of("UTC"));

    @Override
    public OrderCreatedEvent validateAndInitOrder(Order order, Restaurant restaurant,
                                                  DomainEventPublisher<OrderCreatedEvent> orderCreatedDomainEventPublisher) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initialized", order.getId().getValue());

        return new OrderCreatedEvent(order, CURRENT_UTC_TIME, orderCreatedDomainEventPublisher);
    }

    @Override
    public OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidDomainEventPublisher) {
        order.pay();
        log.info("Order with id: {} is paid", order.getId().getValue());

        return new OrderPaidEvent(order, CURRENT_UTC_TIME, orderPaidDomainEventPublisher);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages,
                                                  DomainEventPublisher<OrderCancelledEvent> orderCancelledDomainEventPublisher) {
        order.initCancel(failureMessages);
        log.info("Payment of order with id: {} is cancelling", order.getId().getValue());

        return new OrderCancelledEvent(order, CURRENT_UTC_TIME, orderCancelledDomainEventPublisher);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive())
            throw new OrderDomainException(String.format("Restaurant with id: %s is NOT active", restaurant.getId().getValue()));
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        Map<ProductId, Product> restaurantProducts = restaurant.getProducts().stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        order.getOrderItems().stream()
                .map(OrderItem::getProduct)
                .filter(product -> restaurantProducts.containsKey(product.getId()))
                .forEach(product -> product.update(product.getName(), product.getPrice()));
    }
}
