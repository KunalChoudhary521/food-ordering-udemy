package com.food.ordering.payment.domain.outbox.scheduler;

import com.food.ordering.outbox.OutboxScheduler;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;
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
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> orderOutboxMessageResponse =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);

        if(orderOutboxMessageResponse.isPresent()) {
            List<OrderOutboxMessage> outboxMessages = orderOutboxMessageResponse.get();
            String messagePayloads = outboxMessages.stream().map(OrderOutboxMessage::getPayload).collect(Collectors.joining("\n"));
            log.info("Received {} OrderOutboxMessage for clean-up. The payloads: {}", outboxMessages.size(), messagePayloads);

            orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
            log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
        }

    }
}
