package com.food.ordering.restaurant.domain.outbox.scheduler;

import com.food.ordering.outbox.OutboxScheduler;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.restaurant.domain.port.output.publisher.RestaurantApprovalResponsePublisher;
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
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;
    private final RestaurantApprovalResponsePublisher restaurantApprovalResponsePublisher;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${restaurant-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${restaurant-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> outboxMessagesResponse =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if (outboxMessagesResponse.isPresent() && outboxMessagesResponse.get().size() > 0) {
            List<OrderOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            String messageIds = outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(","));

            log.info("Restaurant - Received {} OrderOutboxMessage with ids {}, sending to message bus!", outboxMessages.size(), messageIds);

            outboxMessages.forEach(orderOutboxMessage -> restaurantApprovalResponsePublisher.publish(orderOutboxMessage,
                    orderOutboxHelper::updateOutboxStatus));
            log.info("Restaurant - {} OrderOutboxMessage sent to message bus!", outboxMessages.size());
        }

    }
}
