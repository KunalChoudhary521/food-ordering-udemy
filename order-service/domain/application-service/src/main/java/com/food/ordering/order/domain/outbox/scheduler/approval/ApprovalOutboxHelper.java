package com.food.ordering.order.domain.outbox.scheduler.approval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.order.domain.port.output.repository.ApprovalOutboxRepository;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;

@AllArgsConstructor
@Slf4j
@Component
public class ApprovalOutboxHelper {

    private final ApprovalOutboxRepository approvalOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getOrderApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        Optional.ofNullable(approvalOutboxRepository.save(orderApprovalOutboxMessage))
                .ifPresentOrElse(
                        message -> log.info("OrderApprovalOutboxMessage saved with outbox id: {}", message.getId()),
                        () -> {
                            String errorMessage = String.format("Could not save OrderApprovalOutboxMessage with outbox id: %s",
                                    orderApprovalOutboxMessage.getId().toString());
                            log.error(errorMessage);
                            throw new OrderDomainException(errorMessage);
                        });
    }

    @Transactional
    public void saveOrderApprovalOutboxMessage(OrderApprovalEventPayload approvalEventPayload, OrderStatus orderStatus,
                                               SagaStatus sagaStatus, OutboxStatus outboxStatus, UUID sagaId) {
        save(OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(approvalEventPayload.getCreatedAt())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(approvalEventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build());

    }

    @Transactional
    public void deleteOrderApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    private String createPayload(OrderApprovalEventPayload approvalEventPayload) {
        try {
            return objectMapper.writeValueAsString(approvalEventPayload);
        } catch (JsonProcessingException e) {
            String errorMessage = String.format("Could not create OrderApprovalEventPayload object for order id: %s",
                    approvalEventPayload.getOrderId());
            log.error(errorMessage, e);
            throw new OrderDomainException(errorMessage, e);
        }
    }
}
