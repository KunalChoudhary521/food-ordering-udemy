package com.food.ordering.order.data.access.outbox.approval.mapper;

import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ApprovalOutboxEntityMapper {

    ApprovalOutboxEntity orderApprovalOutboxMessageToApprovalOutboxEntity(OrderApprovalOutboxMessage orderApprovalOutboxMessage);

    OrderApprovalOutboxMessage approvalOutboxEntityToOrderApprovalOutboxMessage(ApprovalOutboxEntity approvalOutboxEntity);
}
