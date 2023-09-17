package com.food.ordering.payment.domain.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.payment.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RestaurantMapper extends MoneyMapper {

    @Mapping(target = "orderDetail.orderId", source = "restaurantApprovalRequest.orderId")
    @Mapping(target = "orderDetail.orderStatus", source = "restaurantApprovalRequest.restaurantOrderStatus")
    @Mapping(target = "orderDetail.totalAmount", source = "restaurantApprovalRequest.price")
    @Mapping(target = "orderDetail.products", source = "restaurantApprovalRequest.products")
    Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest);

    @ValueMapping(target = MappingConstants.NULL, source = MappingConstants.ANY_REMAINING)
    OrderStatus restaurantOrderStatusToOrderStatus(RestaurantOrderStatus restaurantOrderStatus);

    // TODO: Move mappers to respective domain classes as simple class methods
    RestaurantId toRestaurantId(String value);
    OrderId toOrderId(String value);

}
