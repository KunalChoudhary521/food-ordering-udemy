package com.food.ordering.restaurant.data.access.restaurant.outbox.repository;

import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.data.access.restaurant.outbox.entity.OrderOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {

    Optional<List<OrderOutboxEntity>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    Optional<OrderOutboxEntity> findByTypeAndSagaIdAndOutboxStatus(String type, UUID sagaId, OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
