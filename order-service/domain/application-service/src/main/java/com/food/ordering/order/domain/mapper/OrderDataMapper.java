package com.food.ordering.order.domain.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.create.OrderAddress;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {//TODO: Replace code with MapStruct library

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getOrderAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .orderItems(orderItemsToOrderItemsEntity(createOrderCommand.getOrderItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .trackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .trackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemsEntity(List<com.food.ordering.order.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::orderItemOrderItemEntity)
                .toList();
    }

    private OrderItem orderItemOrderItemEntity(com.food.ordering.order.domain.dto.create.OrderItem orderItem) {
        return OrderItem.builder()
                .product(new Product(new ProductId(orderItem.getProductId())))
                .price(new Money(orderItem.getPrice()))
                .quantity(orderItem.getQuantity())
                .subTotal(new Money(orderItem.getSubTotal()))
                .build();
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(
                UUID.randomUUID(), // TODO: what if street address already exists?
                orderAddress.getStreet(),
                orderAddress.getCity(),
                orderAddress.getCountry(),
                orderAddress.getPostalCode());
    }
}
