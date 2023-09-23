package com.food.ordering.restaurant.messaging.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.payment.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.restaurant.domain.entity.Product;
import com.food.ordering.restaurant.domain.event.OrderApprovedEvent;
import com.food.ordering.restaurant.domain.event.OrderRejectedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RestaurantMessagingMapper extends MoneyMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "restaurantId", source = "event.restaurantId.value")
    @Mapping(target = "orderId", source = "event.orderApproval.orderId.value")
    com.food.ordering.kafka.order.model.RestaurantApprovalResponse orderApprovedEventToRestaurantApprovalResponseAvroModel(OrderApprovedEvent event);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "restaurantId", source = "event.restaurantId.value")
    @Mapping(target = "orderId", source = "event.orderApproval.orderId.value")
    com.food.ordering.kafka.order.model.RestaurantApprovalResponse orderRejectedEventToRestaurantApprovalResponseAvroModel(OrderRejectedEvent event);

    @Mapping(target = "sagaId", expression = "java(\"\")")
    RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(
            com.food.ordering.kafka.order.model.RestaurantApprovalRequest avroModel);

    @Mapping(target = "productId", source = "avroModel.id")
    Product productAvroModelToProduct(com.food.ordering.kafka.order.model.Product avroModel);

    ProductId toProductId(String value); // TODO: move to common mapper class/interface

    default Instant map(ZonedDateTime value) {
        return value.toInstant();
    }
}
