package com.food.ordering.payment.data.access.outbox.mapper;

import com.food.ordering.payment.data.access.outbox.entity.OrderOutboxEntity;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderOutboxEntityMapper {

    OrderOutboxEntity orderOutboxMessageToOrderOutboxEntity(OrderOutboxMessage orderOutboxMessage);

    OrderOutboxMessage orderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity orderOutboxEntity);
}
