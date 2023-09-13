package com.food.ordering.payment.data.access.payment.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.payment.data.access.payment.entity.PaymentEntity;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.valueobject.PaymentId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentDataMapper extends MoneyMapper {

    @Mapping(target = "id", source = "payment.id.value")
    @Mapping(target = "customerId", source = "payment.customerId.value")
    @Mapping(target = "orderId", source = "payment.orderId.value")
    @Mapping(target = "status", source = "payment.paymentStatus")
    PaymentEntity paymentToPaymentEntity(Payment payment);

    @Mapping(target = "paymentStatus", source = "paymentEntity.status")
    @Mapping(target = "paymentId", source = "paymentEntity.id")
    Payment paymentEntityToPayment(PaymentEntity paymentEntity);

    OrderId toOrderId(UUID value); // TODO: move to common mapper class/interface

    CustomerId toCustomerId(UUID value);

    PaymentId toPaymentId(UUID value);
}
