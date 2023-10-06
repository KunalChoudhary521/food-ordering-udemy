package com.food.ordering.payment.messaging.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.kafka.order.model.PaymentResponse;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.outbox.model.OrderEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentMessagingMapper extends MoneyMapper {

    PaymentRequest paymentRequestAvroModelToPaymentRequest(com.food.ordering.kafka.order.model.PaymentRequest paymentRequest);

    default Instant map(ZonedDateTime value) {
        return value.toInstant();
    }

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    PaymentResponse orderEventPayloadToPaymentResponseAvroModel(String sagaId, OrderEventPayload orderEventPayload);
}
