package com.food.ordering.payment.domain.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.event.PaymentEvent;
import com.food.ordering.payment.domain.outbox.model.OrderEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentMapper extends MoneyMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    Payment paymentRequestModelToPayment(PaymentRequest paymentRequest);

    @Mapping(target = "paymentId", source = "paymentEvent.payment.id.value")
    @Mapping(target = "customerId", source = "paymentEvent.payment.customerId.value")
    @Mapping(target = "orderId", source = "paymentEvent.payment.orderId.value")
    @Mapping(target = "price", source = "paymentEvent.payment.price")
    @Mapping(target = "paymentStatus", source = "paymentEvent.payment.paymentStatus")
    OrderEventPayload paymentEventToOrderEventPayload(PaymentEvent paymentEvent);

    // TODO: Move mappers to respective domain classes as simple class methods
    OrderId map(String value);
    CustomerId from(String value);
}
