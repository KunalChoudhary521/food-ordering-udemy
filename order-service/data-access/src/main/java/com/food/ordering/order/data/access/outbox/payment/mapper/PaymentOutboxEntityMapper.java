package com.food.ordering.order.data.access.outbox.payment.mapper;

import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentOutboxEntityMapper {

    PaymentOutboxEntity orderPaymentOutboxMessageToPaymentOutboxEntity(OrderPaymentOutboxMessage orderPaymentOutboxMessage);

    OrderPaymentOutboxMessage paymentOutboxEntityToOrderPaymentOutboxMessage(PaymentOutboxEntity paymentOutboxEntity);
}
