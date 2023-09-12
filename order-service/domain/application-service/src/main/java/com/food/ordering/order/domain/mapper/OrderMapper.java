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

    Product orderItemDtoToProduct(OrderItemDto orderItemsDtos);

    @Mapping(target = "trackingId", source = "order.trackingId.value")
    CreateOrderResponse orderToCreateOrderResponse(Order order, String message);

    @Mapping(target = "trackingId", source = "order.trackingId.value")
    TrackOrderResponse orderToTrackOrderResponse(Order order);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress);

    @Mapping(target = "product.productId", source = "orderItem.productId")
    OrderItem toOrderItem(OrderItemDto orderItem);
}
