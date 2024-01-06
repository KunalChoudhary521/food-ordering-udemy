package com.food.ordering.order.domain.entity;

import com.food.ordering.domain.entity.AggregateRoot;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.valueobject.OrderItemId;
import com.food.ordering.order.domain.valueobject.StreetAddress;
import com.food.ordering.order.domain.valueobject.TrackingId;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> orderItems;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    @Builder
    public Order(OrderId id, CustomerId customerId, RestaurantId restaurantId,
                 StreetAddress deliveryAddress, Money price, List<OrderItem> orderItems,
                 TrackingId trackingId, OrderStatus orderStatus, List<String> failureMessages) {
        super.setId(id);
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.orderItems = orderItems;
        this.trackingId = trackingId;
        this.orderStatus = orderStatus;
        this.failureMessages = failureMessages != null ? new ArrayList<>(failureMessages) : new ArrayList<>();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order not in correct state for pay operation");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order not in correct state for approve operation");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order not in correct state for cancelling operation");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PENDING && orderStatus != OrderStatus.CANCELLING) {
            throw new OrderDomainException("Order not in correct state for cancel operation");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Order is not in correct state for initialization");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero");
        }
    }

    private void validateItemsPrice() {
        Money orderItemsSum = orderItems.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsSum)) {
            throw new OrderDomainException(String.format("Total price: %.2f is not equal to items total: %.2f",
                    price.getAmount(), orderItemsSum.getAmount()));
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException(String.format("Order item price: %.2f is invalid for product %s",
                    orderItem.getPrice().getAmount(), orderItem.getProduct().getId().getValue()));
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem : orderItems) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            List<String> nonEmptyMessages = failureMessages.stream().filter(String::isEmpty).toList();
            this.failureMessages.addAll(nonEmptyMessages);
        }

        if (this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }
}
