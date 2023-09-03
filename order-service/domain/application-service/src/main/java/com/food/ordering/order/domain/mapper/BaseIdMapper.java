package com.food.ordering.order.domain.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.order.domain.valueobject.OrderItemId;
import com.food.ordering.order.domain.valueobject.TrackingId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BaseIdMapper {

    TrackingId toTrackingId(UUID value);

    ProductId toProductId(UUID value);

    OrderId toOrderId(UUID value);

    CustomerId toCustomerId(UUID value);

    RestaurantId toRestaurantId(UUID value);

    OrderItemId toOrderItemId(Long value);

    default Long toLong(OrderItemId value) {
        return value.getValue();
    }
}
