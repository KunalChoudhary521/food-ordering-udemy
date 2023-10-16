package com.food.ordering.order.domain.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.create.OrderAddress;
import com.food.ordering.order.domain.dto.create.OrderItemDto;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.order.domain.valueobject.StreetAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMapper extends MoneyMapper, BaseIdMapper {

    @Mapping(target = "deliveryAddress", source = "createOrderCommand.orderAddress")
    Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand);

    @Mapping(target = "id", source = "createOrderCommand.restaurantId")
    @Mapping(target = "products", source = "createOrderCommand.orderItems")
    Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand);

    Product orderItemDtoToProduct(OrderItemDto orderItemDto);

    @Mapping(target = "trackingId", source = "order.trackingId.value")
    CreateOrderResponse orderToCreateOrderResponse(Order order, String message);

    @Mapping(target = "trackingId", source = "order.trackingId.value")
    TrackOrderResponse orderToTrackOrderResponse(Order order);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress);

    @Mapping(target = "product.productId", source = "orderItemDto.productId")
    @Mapping(target = "product.price", source = "orderItemDto.price")
    OrderItem toOrderItem(OrderItemDto orderItemDto);

    @Mapping(target = "orderId", source = "orderCreatedEvent.order.id.value")
    @Mapping(target = "customerId", source = "orderCreatedEvent.order.customerId.value")
    @Mapping(target = "price", source = "orderCreatedEvent.order.price")
    @Mapping(target = "paymentOrderStatus", expression = "java(com.food.ordering.domain.valueobject.PaymentOrderStatus.PENDING.name())")
    OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent);

    @Mapping(target = "orderId", source = "orderPaidEvent.order.id.value")
    @Mapping(target = "restaurantId", source = "orderPaidEvent.order.restaurantId.value")
    @Mapping(target = "restaurantOrderStatus", expression = "java(com.food.ordering.domain.valueobject.RestaurantOrderStatus.PAID.name())")
    @Mapping(target = "products", source = "orderPaidEvent.order.orderItems")
    @Mapping(target = "price", source = "orderPaidEvent.order.price")
    OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent);

    @Mapping(target = "id", source = "orderItem.product.id.value")
    OrderApprovalEventProduct orderItemToOrderApprovalEventProduct(OrderItem orderItem);

    @Mapping(target = "orderId", source = "orderCancelledEvent.order.id.value")
    @Mapping(target = "customerId", source = "orderCancelledEvent.order.customerId.value")
    @Mapping(target = "price", source = "orderCancelledEvent.order.price")
    @Mapping(target = "paymentOrderStatus", expression = "java(com.food.ordering.domain.valueobject.PaymentOrderStatus.CANCELLED.name())")
    OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent);
}
