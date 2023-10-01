package com.food.ordering.order.domain;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.mapper.OrderMapper;
import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
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
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
        Optional<OrderApprovalOutboxMessage> approvalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()), SagaStatus.PROCESSING);

        if(approvalOutboxMessageResponse.isEmpty()) {
            log.info("The outbox message with saga id: {} has already been processed", restaurantApprovalResponse.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = approvalOutboxMessageResponse.get();
        Order order = approveOrder(restaurantApprovalResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        approvalOutboxHelper.save(updateOrderApprovalOutboxMessage(orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus));

        paymentOutboxHelper.save(updateOrderPaymentOutboxMessage(restaurantApprovalResponse.getSagaId(), order.getOrderStatus(), sagaStatus));

        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        UUID sagaId = UUID.fromString(restaurantApprovalResponse.getSagaId());
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(sagaId, SagaStatus.PROCESSING);

        if(orderApprovalOutboxMessageResponse.isEmpty()) {
            log.info("The outbox message with saga id: {} has already been rolled back", restaurantApprovalResponse.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        OrderCancelledEvent orderCancelledEvent = rollbackCancelledOrder(restaurantApprovalResponse);
        OrderStatus orderStatus = orderCancelledEvent.getOrder().getOrderStatus();
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderStatus);

        approvalOutboxHelper.save(updateOrderApprovalOutboxMessage(orderApprovalOutboxMessage, orderStatus, sagaStatus));

        OrderPaymentEventPayload orderPaymentEventPayload = orderMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledEvent);
        paymentOutboxHelper.savePaymentOutboxMessage(orderPaymentEventPayload, orderStatus, sagaStatus, OutboxStatus.STARTED, sagaId);

        log.info("Order with id: {} is cancelling", orderCancelledEvent.getOrder().getId().getValue());
    }

    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        return orderSagaHelper.save(order);
    }

    private OrderCancelledEvent rollbackCancelledOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.save(order);
        return orderCancelledEvent;
    }

    private OrderApprovalOutboxMessage updateOrderApprovalOutboxMessage(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                                                                        OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);

        return orderApprovalOutboxMessage;
    }

    private OrderPaymentOutboxMessage updateOrderPaymentOutboxMessage(String sagaId, OrderStatus orderStatus,
                                                                      SagaStatus sagaStatus) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(sagaId), SagaStatus.PROCESSING);

        if(orderPaymentOutboxMessageResponse.isEmpty()) {
            String errorMessage = String.format("Payment outbox message not in %s status!", SagaStatus.PROCESSING.name());
            log.error(errorMessage);
            throw new OrderDomainException(errorMessage);
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);

        return orderPaymentOutboxMessage;
    }
}
