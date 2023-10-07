package com.food.ordering.restaurant.data.access.restaurant.outbox.adapter;

import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.data.access.restaurant.outbox.entity.OrderOutboxEntity;
import com.food.ordering.restaurant.data.access.restaurant.outbox.exception.OrderOutboxNotFoundException;
import com.food.ordering.restaurant.data.access.restaurant.outbox.mapper.OrderOutboxEntityMapper;
import com.food.ordering.restaurant.data.access.restaurant.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.restaurant.domain.port.output.repository.OrderOutboxRepository;
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
                .orElseThrow(() -> new OrderOutboxNotFoundException("Restaurant approval outbox object not found for saga type " + sagaType))
                .stream()
                .map(orderOutboxEntityMapper::orderOutboxEntityToOrderOutboxMessage)
                .toList());
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(String sagaType, UUID sagaId, OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(sagaType, sagaId, outboxStatus)
                .map(orderOutboxEntityMapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus);
    }
}
