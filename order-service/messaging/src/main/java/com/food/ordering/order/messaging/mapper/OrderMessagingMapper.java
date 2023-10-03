package com.food.ordering.order.messaging.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.kafka.order.model.RestaurantApprovalRequest;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMessagingMapper extends MoneyMapper {

    default Instant map(ZonedDateTime value) {
        return value.toInstant();
    }

    PaymentResponse paymentResponseAvroModelToPaymentResponse(com.food.ordering.kafka.order.model.PaymentResponse paymentResponse);

    RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(com.food.ordering.kafka.order.model.RestaurantApprovalResponse restaurantApprovalResponseAvroModel);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    PaymentRequest orderPaymentEventPayloadToPaymentRequestAvroModel(String sagaId, OrderPaymentEventPayload orderPaymentEventPayload);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    RestaurantApprovalRequest orderApprovalEventPayloadToRestaurantApprovalRequestAvroModel(String sagaId,
                                                                                            OrderApprovalEventPayload orderApprovalEventPayload);
}
