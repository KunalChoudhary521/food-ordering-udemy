package com.food.ordering.restaurant.data.access.restaurant.outbox.mapper;

import com.food.ordering.restaurant.data.access.restaurant.outbox.entity.OrderOutboxEntity;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderOutboxEntityMapper {

    OrderOutboxEntity orderOutboxMessageToOrderOutboxEntity(OrderOutboxMessage orderOutboxMessage);

    OrderOutboxMessage orderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity orderOutboxEntity);
}
