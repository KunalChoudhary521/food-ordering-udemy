package com.food.ordering.order.data.access.outbox.payment.adapter;

import com.food.ordering.order.data.access.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.order.data.access.outbox.payment.exception.PaymentOutboxNotFoundException;
import com.food.ordering.order.data.access.outbox.payment.mapper.PaymentOutboxEntityMapper;
import com.food.ordering.order.data.access.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.order.domain.port.output.repository.PaymentOutboxRepository;
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
public class PaymentOutboxJpaRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentOutboxEntityMapper paymentOutboxEntityMapper;

    @Override
    public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
        PaymentOutboxEntity paymentOutboxEntity = paymentOutboxEntityMapper.orderPaymentOutboxMessageToPaymentOutboxEntity(orderPaymentOutboxMessage);
        PaymentOutboxEntity savedPaymentOutboxEntity = paymentOutboxJpaRepository.save(paymentOutboxEntity);
        return paymentOutboxEntityMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(savedPaymentOutboxEntity);
    }

    @Override
    public Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus,
                                                                                            SagaStatus... sagaStatus) {
        return Optional.of(paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, List.of(sagaStatus))
                .orElseThrow(() -> new PaymentOutboxNotFoundException("No Payment outbox entity found for saga type: " + sagaType))
                .stream()
                .map(paymentOutboxEntityMapper::paymentOutboxEntityToOrderPaymentOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String sagaType, UUID sagaId, SagaStatus... sagaStatus) {
        return paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(sagaType, sagaId, List.of(sagaStatus))
                .map(paymentOutboxEntityMapper::paymentOutboxEntityToOrderPaymentOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, List.of(sagaStatus));
    }
}
