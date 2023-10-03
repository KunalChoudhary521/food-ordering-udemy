package com.food.ordering.payment.data.access.outbox.adapter;

import com.food.ordering.domain.valueobject.PaymentStatus;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.payment.data.access.outbox.entity.OrderOutboxEntity;
import com.food.ordering.payment.data.access.outbox.exception.OrderOutboxNotFoundException;
import com.food.ordering.payment.data.access.outbox.mapper.OrderOutboxEntityMapper;
import com.food.ordering.payment.data.access.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.payment.domain.port.output.repository.OrderOutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxEntityMapper orderOutboxEntityMapper;

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
        OrderOutboxEntity orderOutboxEntity = orderOutboxEntityMapper.orderOutboxMessageToOrderOutboxEntity(orderOutboxMessage);
        OrderOutboxEntity savedOrderOutboxEntity = orderOutboxJpaRepository.save(orderOutboxEntity);
        return orderOutboxEntityMapper.orderOutboxEntityToOrderOutboxMessage(savedOrderOutboxEntity);
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        return Optional.of(orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus)
                .orElseThrow(() -> new OrderOutboxNotFoundException("Approval outbox object " +
                        "cannot be found for saga type " + sagaType))
                .stream()
                .map(orderOutboxEntityMapper::orderOutboxEntityToOrderOutboxMessage)
                .toList());
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String sagaType, UUID sagaId,
                                                                                           PaymentStatus paymentStatus,
                                                                                           OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(sagaType, sagaId, paymentStatus, outboxStatus)
                .map(orderOutboxEntityMapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus);
    }
}
