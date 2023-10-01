package com.food.ordering.order.domain.outbox.scheduler.approval;

import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.outbox.OutboxScheduler;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Component
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;

    @Override
    @Scheduled(cron = "@midnight") // TODO: Add zone and read property from application.yml
    public void processOutboxMessage() {
        Optional<List<OrderApprovalOutboxMessage>> outboxMessagesResponse =
                approvalOutboxHelper.getOrderApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED,
                        SagaStatus.COMPENSATED);

        if (outboxMessagesResponse.isPresent()) {
            List<OrderApprovalOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            String messagePayloads = outboxMessages.stream().map(OrderApprovalOutboxMessage::getPayload).collect(Collectors.joining("\n"));
            log.info("Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}", outboxMessages.size(), messagePayloads);

            approvalOutboxHelper.deleteOrderApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                    OutboxStatus.COMPLETED,
                    SagaStatus.SUCCEEDED,
                    SagaStatus.FAILED,
                    SagaStatus.COMPENSATED);
            log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
        }
    }
}
