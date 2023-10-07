package com.food.ordering.restaurant.domain.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.restaurant.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderApprovalEvent;
import com.food.ordering.restaurant.domain.outbox.model.OrderEventPayload;
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

    @Mapping(target = "orderId", source = "orderApprovalEvent.orderApproval.orderId.value")
    @Mapping(target = "restaurantId", source = "orderApprovalEvent.restaurantId.value")
    @Mapping(target = "orderApprovalStatus", source = "orderApprovalEvent.orderApproval.approvalStatus")
    OrderEventPayload orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent);

    // TODO: Move mappers to respective domain classes as simple class methods
    RestaurantId toRestaurantId(String value);
    OrderId toOrderId(String value);

}
