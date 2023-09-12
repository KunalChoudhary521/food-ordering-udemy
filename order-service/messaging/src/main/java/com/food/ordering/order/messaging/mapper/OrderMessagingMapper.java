package com.food.ordering.order.messaging.mapper;

import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.kafka.order.model.Product;
import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.domain.mapper.MoneyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMessagingMapper extends MoneyMapper {

    @Mapping(target = "customerId", source = "event.order.customerId.value")
    @Mapping(target = "orderId", source = "event.order.id.value")
    @Mapping(target = "price", source = "event.order.price")
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "paymentOrderStatus", expression = "java(com.food.ordering.kafka.order.model.PaymentOrderStatus.PENDING)")
    PaymentRequest orderCreatedEventToPaymentRequest(OrderCreatedEvent event);

    @Mapping(target = "customerId", source = "event.order.customerId.value")
    @Mapping(target = "orderId", source = "event.order.id.value")
    @Mapping(target = "price", source = "event.order.price")
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "paymentOrderStatus", expression = "java(com.food.ordering.kafka.order.model.PaymentOrderStatus.CANCELLED)")
    PaymentRequest orderCancelledEventToPaymentRequest(OrderCancelledEvent event);

    default Instant map(ZonedDateTime value) {
        return value.toInstant();
    }

    @Mapping(target = "restaurantId", source = "event.order.restaurantId.value")
    @Mapping(target = "orderId", source = "event.order.id.value")
    @Mapping(target = "price", source = "event.order.price")
    @Mapping(target = "products", source = "event.order.orderItems")
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "restaurantOrderStatus", expression = "java(com.food.ordering.kafka.order.model.RestaurantOrderStatus.PAID)")
    RestaurantApprovalRequest orderPaidEventToRestaurantApprovalRequest(OrderPaidEvent event);

    @Mapping(target = "id", source = "orderItem.product.id.value")
    @Mapping(target = "quantity", source = "orderItem.quantity")
    Product orderItemToProductAvroModel(OrderItem orderItem);

    PaymentResponse paymentResponseAvroModelToPaymentResponse(com.food.ordering.kafka.order.model.PaymentResponse paymentResponse);

    RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(com.food.ordering.kafka.order.model.RestaurantApprovalResponse restaurantApprovalResponseAvroModel);
}
