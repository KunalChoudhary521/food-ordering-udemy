package com.food.ordering.order.domain.outbox.scheduler.approval;

import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.order.domain.port.output.publisher.RestaurantApprovalRequestPublisher;
import com.food.ordering.outbox.OutboxScheduler;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Component
public class RestaurantApprovalOutboxScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final RestaurantApprovalRequestPublisher restaurantApprovalRequestPublisher;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderApprovalOutboxMessage>> outboxMessagesResponse =
                approvalOutboxHelper.getOrderApprovalOutboxMessagesByOutboxStatusAndSagaStatus(OutboxStatus.STARTED,
                        SagaStatus.PROCESSING);

        if(outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<OrderApprovalOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            String messageIds = outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(","));

            log.info("Received {} OrderApprovalOutboxMessage with ids: {}, sending to message bus!", outboxMessages.size(), messageIds);

            outboxMessages.forEach(outboxMessage -> restaurantApprovalRequestPublisher.publish(outboxMessage, this::updateOutboxStatus));
            log.info("{} OrderApprovalOutboxMessage sent to message bus!", outboxMessages.size());
        }

    }

    private void updateOutboxStatus(OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
        orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
        approvalOutboxHelper.save(orderApprovalOutboxMessage);
        log.info("OrderApprovalOutboxMessage is updated with outbox status: {}", outboxStatus.name());
    }
}
