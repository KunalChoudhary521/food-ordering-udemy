package com.food.ordering.order.data.access.outbox.approval.adapter;

import com.food.ordering.order.data.access.outbox.approval.entity.ApprovalOutboxEntity;
import com.food.ordering.order.data.access.outbox.approval.exception.ApprovalOutboxNotFoundException;
import com.food.ordering.order.data.access.outbox.approval.mapper.ApprovalOutboxEntityMapper;
import com.food.ordering.order.data.access.outbox.approval.repository.ApprovalOutboxJpaRepository;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.order.domain.port.output.repository.ApprovalOutboxRepository;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {

    private final ApprovalOutboxJpaRepository approvalOutboxJpaRepository;
    private final ApprovalOutboxEntityMapper approvalOutboxEntityMapper;

    @Override
    public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        ApprovalOutboxEntity approvalOutboxEntity = approvalOutboxEntityMapper.orderApprovalOutboxMessageToApprovalOutboxEntity(orderApprovalOutboxMessage);
        ApprovalOutboxEntity savedApprovalOutboxEntity = approvalOutboxJpaRepository.save(approvalOutboxEntity);
        return approvalOutboxEntityMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(savedApprovalOutboxEntity);
    }

    @Override
    public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType,
                                                                                             OutboxStatus outboxStatus,
                                                                                             SagaStatus... sagaStatus) {
        return Optional.of(approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, List.of(sagaStatus))
                .orElseThrow(() -> new ApprovalOutboxNotFoundException("No Approval outbox entity found for saga type: " + sagaType))
                .stream()
                .map(approvalOutboxEntityMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String sagaType, UUID sagaId, SagaStatus... sagaStatus) {
        return approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(sagaType, sagaId, List.of(sagaStatus))
                .map(approvalOutboxEntityMapper::approvalOutboxEntityToOrderApprovalOutboxMessage);

    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, List.of(sagaStatus));
    }
}
