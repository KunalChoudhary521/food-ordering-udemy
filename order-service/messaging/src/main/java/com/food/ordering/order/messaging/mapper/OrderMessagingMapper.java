package com.food.ordering.order.messaging.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.kafka.order.model.PaymentOrderStatus;
import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.kafka.order.model.Product;
import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.event.OrderEvent;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMessagingMapper extends MoneyMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "customerId", source = "event.order.customerId.value")
    @Mapping(target = "orderId", source = "event.order.id.value")
    @Mapping(target = "price", source = "event.order.price")
    @Mapping(target = "paymentOrderStatus", source = "event.order.orderStatus")
    PaymentRequest orderEventToPaymentRequest(OrderEvent event);

    @ValueMapping(target = MappingConstants.NULL, source = MappingConstants.ANY_REMAINING)
    PaymentOrderStatus orderStatusToPaymentOrderStatus(OrderStatus orderStatus);

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
