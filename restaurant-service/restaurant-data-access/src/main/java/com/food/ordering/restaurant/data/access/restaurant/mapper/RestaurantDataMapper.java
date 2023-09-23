package com.food.ordering.restaurant.data.access.restaurant.mapper;

import com.food.ordering.common.data.access.restaurant.entity.RestaurantEntity;
import com.food.ordering.common.data.access.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.restaurant.data.access.restaurant.entity.OrderApprovalEntity;
import com.food.ordering.restaurant.domain.entity.OrderApproval;
import com.food.ordering.restaurant.domain.entity.OrderDetail;
import com.food.ordering.restaurant.domain.entity.Product;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.valueobject.OrderApprovalId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RestaurantDataMapper {

    @Mapping(target = "id", source = "orderApproval.id.value")
    @Mapping(target = "restaurantId", source = "orderApproval.restaurantId.value")
    @Mapping(target = "orderId", source = "orderApproval.orderId.value")
    @Mapping(target = "status", source = "orderApproval.approvalStatus")
    OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval);

    @Mapping(target = "orderApprovalId", source = "orderApprovalEntity.id")
    @Mapping(target = "approvalStatus", source = "orderApprovalEntity.status")
    OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity);

    OrderApprovalId toOrderApprovalId(UUID value); // TODO: move to common mapper class/interface
    RestaurantId toRestaurantId(UUID value);
    OrderId toOrderId(UUID value);

    default Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream().findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("No restaurants found!"));

        List<Product> restaurantProducts = restaurantEntities.stream()
                .map(entity -> Product.builder()
                                .productId(new ProductId(entity.getProductId()))
                                .name(entity.getProductName())
                                .price(new Money(entity.getProductPrice()))
                                .available(entity.getProductAvailable())
                                .build())
                .toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getId()))
                .orderDetail(OrderDetail.builder().products(restaurantProducts).build())
                .active(restaurantEntity.getActive())
                .build();
    }

    default List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getOrderDetail().getProducts().stream().map(product -> product.getId().getValue()).toList();
    }
}
