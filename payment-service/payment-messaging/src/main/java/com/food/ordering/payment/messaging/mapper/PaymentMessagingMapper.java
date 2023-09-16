package com.food.ordering.payment.messaging.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.event.PaymentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentMessagingMapper extends MoneyMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "sagaId", expression = "java(\"\")")
    @Mapping(target = "paymentId", source = "paymentEvent.payment.id.value")
    @Mapping(target = "customerId", source = "paymentEvent.payment.customerId.value")
    @Mapping(target = "orderId", source = "paymentEvent.payment.orderId.value")
    @Mapping(target = "price", source = "paymentEvent.payment.price")
    @Mapping(target = "paymentStatus", source = "paymentEvent.payment.paymentStatus")
    @Mapping(target = "failureMessages", source = "paymentEvent.failureMessages")
    PaymentResponse paymentEventToPaymentResponseAvroModel(PaymentEvent paymentEvent);

    PaymentRequest paymentRequestAvroModelToPaymentRequest(com.food.ordering.kafka.order.model.PaymentRequest paymentRequest);

    default Instant map(ZonedDateTime value) {
        return value.toInstant();
    }
}
