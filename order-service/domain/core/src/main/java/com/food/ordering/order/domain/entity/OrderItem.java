package com.food.ordering.order.domain.entity;

import com.food.ordering.domain.entity.BaseEntity;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.order.domain.valueobject.OrderItemId;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItem extends BaseEntity<OrderItemId> {

    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money subTotal;
    private final Money price;

    @Builder
    public OrderItem(OrderItemId id, OrderId orderId, Product product, int quantity, Money subTotal, Money price) {
        super.setId(id);
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.subTotal = subTotal;
        this.price = price;
    }

    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    boolean isPriceValid() {
        return price.isGreaterThanZero() &&
                price.equals(product.getPrice()) &&
                price.multiply(quantity).equals(subTotal);
    }
}
