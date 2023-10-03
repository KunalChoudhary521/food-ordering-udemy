package com.food.ordering.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaMessagePublisher {

    private final ObjectMapper objectMapper;

    public <T> T getOrderEventPayload(String payload, Class<T> outputType) {
        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            String errorMessage = String.format("Could not read %s object!", outputType.getName());
            log.error(errorMessage, e);
            throw new OrderDomainException(errorMessage, e);
        }
    }

    public <T, U> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(String topic, T avroModel, U outboxMessage,
                                                                                BiConsumer<U, OutboxStatus> outboxCallback,
                                                                                String orderId, String avroModelName) {
        return (result, ex) -> {
            if (ex == null) {
                log.info("Successfully received response from Kafka for order id {} Topic: {} Partition: {} Offset {} Timestamp {}",
                        orderId, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            } else {
                log.error("Error while publishing {} with message: {} and outbox type: {} to topic {}",
                        avroModelName, avroModel.toString(), outboxMessage.getClass().getName(), topic, ex);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }
        };

    }
}
