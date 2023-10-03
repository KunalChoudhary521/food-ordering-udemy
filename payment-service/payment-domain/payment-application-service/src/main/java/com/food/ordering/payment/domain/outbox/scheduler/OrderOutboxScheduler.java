package com.food.ordering.payment.domain.outbox.scheduler;

import com.food.ordering.outbox.OutboxScheduler;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.payment.domain.port.output.publisher.PaymentResponsePublisher;
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
    private final PaymentResponsePublisher paymentResponsePublisher;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> orderOutboxMessageResponse =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if(orderOutboxMessageResponse.isPresent() && !orderOutboxMessageResponse.get().isEmpty()) {
            List<OrderOutboxMessage> outboxMessages = orderOutboxMessageResponse.get();
            String messageIds = outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(","));

            log.info("Received {} OrderOutboxMessage with ids: {}, sending to message bus!", outboxMessages.size(), messageIds);

            outboxMessages.forEach(outboxMessage -> paymentResponsePublisher.publish(outboxMessage, orderOutboxHelper::updateOutboxMessage));
            log.info("{} OrderOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }
}
