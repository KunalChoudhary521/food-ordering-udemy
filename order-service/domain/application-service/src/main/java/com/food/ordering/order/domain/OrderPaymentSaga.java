package com.food.ordering.order.domain;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.PaymentStatus;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.mapper.OrderMapper;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.order.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.order.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import com.food.ordering.saga.SagaStep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {
        UUID sagaId = UUID.fromString(paymentResponse.getSagaId());
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(sagaId, SagaStatus.STARTED);

        if(orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("The outbox message with saga id: {} has already been processed", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.save(order);

        OrderStatus orderStatus = orderPaidEvent.getOrder().getOrderStatus();
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderStatus);

        paymentOutboxHelper.save(updateOrderPaymentOutboxMessage(orderPaymentOutboxMessage, orderStatus, sagaStatus));

        OrderApprovalEventPayload orderApprovalEventPayload = orderMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent);
        approvalOutboxHelper.saveOrderApprovalOutboxMessage(orderApprovalEventPayload, orderStatus, sagaStatus, OutboxStatus.STARTED, sagaId);

        log.info("Order with id: {} is paid", order.getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(paymentResponse.getSagaId()),
                        getCurrentSagaStatus(paymentResponse.getPaymentStatus()));

        if(orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("The outbox message with saga id: {} has already been rolled back", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
        Order order = rollbackPaymentForOrder(paymentResponse);

        OrderStatus orderStatus = order.getOrderStatus();
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderStatus);

        paymentOutboxHelper.save(updateOrderPaymentOutboxMessage(orderPaymentOutboxMessage, orderStatus, sagaStatus));

        if(paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(updateOrderApprovalOutboxMessage(paymentResponse.getSagaId(), orderStatus, sagaStatus));
        }

        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private OrderPaymentOutboxMessage updateOrderPaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                                                                      OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderApprovalOutboxMessage updateOrderApprovalOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> approvalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(sagaId), SagaStatus.COMPENSATING);

        if(approvalOutboxMessageResponse.isEmpty()) {
            String errorMessage = String.format("Approval outbox message not in %s status!", SagaStatus.COMPENSATING.name());
            log.error(errorMessage);
            throw new OrderDomainException(errorMessage);
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = approvalOutboxMessageResponse.get();
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);

        return orderApprovalOutboxMessage;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        switch (paymentStatus) {
            case COMPLETED:
                return new SagaStatus[]{ SagaStatus.STARTED };
            case CANCELLED:
                return new SagaStatus[]{ SagaStatus.PROCESSING };
            case FAILED:
                return new SagaStatus[] { SagaStatus.STARTED, SagaStatus.PROCESSING };
            default:
                return new SagaStatus[]{};
        }
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        return orderSagaHelper.save(order);
    }
}
