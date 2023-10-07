package com.food.ordering.restaurant.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.domain.exception.RestaurantDomainException;
import com.food.ordering.restaurant.domain.outbox.model.OrderEventPayload;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.restaurant.domain.port.output.repository.OrderOutboxRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;

@AllArgsConstructor
@Slf4j
@Component
public class OrderOutboxHelper {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;


    @Transactional(readOnly = true)
    public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(UUID sagaId, OutboxStatus outboxStatus) {
        return orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(ORDER_SAGA_NAME, sagaId, outboxStatus);
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
                                       OrderApprovalStatus approvalStatus,
                                       OutboxStatus outboxStatus,
                                       UUID sagaId) {
        save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderEventPayload))
                .approvalStatus(approvalStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    @Transactional
    public void updateOutboxStatus(OrderOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        save(orderPaymentOutboxMessage);
        log.info("Restaurant - Order outbox table status is updated as: {}", outboxStatus.name());
    }

    private String createPayload(OrderEventPayload orderEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderEventPayload);
        } catch (JsonProcessingException e) {
            String errorMessage = "Restaurant - Could not create OrderEventPayload json!";
            log.error(errorMessage, e);
            throw new RestaurantDomainException(errorMessage, e);
        }
    }

    private void save(OrderOutboxMessage orderOutboxMessage) {
        Optional.ofNullable(orderOutboxRepository.save(orderOutboxMessage))
                .ifPresentOrElse(message -> log.info("Restaurant - OrderOutboxMessage is saved with id: {}", orderOutboxMessage.getId()),
                        () -> {
                            String errorMessage = "Restaurant - Could not save OrderOutboxMessage";
                            log.error(errorMessage);
                            throw new RestaurantDomainException(errorMessage);
                        });
    }
}
