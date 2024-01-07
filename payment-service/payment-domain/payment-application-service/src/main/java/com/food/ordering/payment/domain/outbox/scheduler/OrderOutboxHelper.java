package com.food.ordering.payment.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.domain.valueobject.PaymentStatus;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.payment.domain.exception.PaymentDomainException;
import com.food.ordering.payment.domain.outbox.model.OrderEventPayload;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.payment.domain.port.output.repository.OrderOutboxRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.domain.constants.CommonConstants.CURRENT_UTC_TIME;
import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;

@AllArgsConstructor
@Slf4j
@Component
public class OrderOutboxHelper {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(UUID sagaId, PaymentStatus paymentStatus) {
        return orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(ORDER_SAGA_NAME, sagaId,
                paymentStatus, OutboxStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderOutboxMessage>> getOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        return orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void deleteOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void saveOrderOutboxMessage(OrderEventPayload orderEventPayload,
                                       PaymentStatus paymentStatus,
                                       OutboxStatus outboxStatus,
                                       UUID sagaId) {
        save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderEventPayload.getCreatedAt())
                .processedAt(CURRENT_UTC_TIME)
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderEventPayload))
                .paymentStatus(paymentStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    @Transactional
    public void updateOutboxMessage(OrderOutboxMessage orderOutboxMessage, OutboxStatus outboxStatus) {
        orderOutboxMessage.setOutboxStatus(outboxStatus);
        save(orderOutboxMessage);
        log.info("Order outbox table status is updated as: {}", outboxStatus.name());
    }

    private String createPayload(OrderEventPayload orderEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderEventPayload);
        } catch (JsonProcessingException e) {
            String errorMessage = "Could not create OrderEventPayload json!";
            log.error(errorMessage, e);
            throw new PaymentDomainException(errorMessage, e);
        }
    }

    private void save(OrderOutboxMessage orderOutboxMessage) {
        Optional.ofNullable(orderOutboxRepository.save(orderOutboxMessage))
                .ifPresentOrElse(message -> log.info("OrderOutboxMessage is saved with id: {}", orderOutboxMessage.getId()),
                        () -> {
                            String errorMessage = "Could not save OrderOutboxMessage";
                            log.error(errorMessage);
                            throw new PaymentDomainException(errorMessage);
                        });
    }
}
