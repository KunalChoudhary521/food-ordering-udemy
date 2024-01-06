package com.food.ordering.order.data.access.order.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.order.data.access.order.entity.OrderAddressEntity;
import com.food.ordering.order.data.access.order.entity.OrderEntity;
import com.food.ordering.order.data.access.order.entity.OrderItemEntity;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.OrderItem;
import com.food.ordering.order.domain.mapper.BaseIdMapper;
import com.food.ordering.order.domain.valueobject.StreetAddress;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderEntityMapper extends MoneyMapper, BaseIdMapper {

    String FAILURE_MESSAGE_DELIMITER = ",";

    @Mapping(target = "deliveryAddress", source = "orderEntity.address")
    @Mapping(target = "orderItems", source = "orderEntity.items")
    Order orderEntityToOrder(OrderEntity orderEntity);

    @InheritInverseConfiguration
    @Mapping(target = "id", source = "order.id.value")
    @Mapping(target = "customerId", source = "order.customerId.value")
    @Mapping(target = "restaurantId", source = "order.restaurantId.value")
    @Mapping(target = "trackingId", source = "order.trackingId.value")
    OrderEntity orderToOrderEntity(Order order);

    StreetAddress orderAddressEntityToStreetAddress(OrderAddressEntity orderAddressEntity);

    @Mapping(target = "product.productId", source = "orderItemEntity.productId")
    OrderItem orderItemEntityToOrderItem(OrderItemEntity orderItemEntity);

    @Mapping(target = "productId", source = "orderItem.product.id.value")
    OrderItemEntity orderItemToOrderItemEntity(OrderItem orderItem);

    default List<String> stringToList(String value) {
        return (value == null) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(value.split(FAILURE_MESSAGE_DELIMITER)));
    }

    default String listToString(List<String> value) {
        return (value == null) ? "" : String.join(FAILURE_MESSAGE_DELIMITER, value);
    }
}
