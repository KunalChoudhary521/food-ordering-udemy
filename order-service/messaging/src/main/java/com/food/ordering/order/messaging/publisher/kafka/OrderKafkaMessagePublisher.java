package com.food.ordering.order.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@Slf4j
public class OrderKafkaMessagePublisher {

    public <T> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(String requestTopic, T requestModel,
                                                                             String orderId, String requestModelName) {
        return (result, ex) -> {
            if (ex == null) {
                log.info("Successfully received response from Kafka for order id {} Topic: {} Partition: {} Offset {} Timestamp {}",
                        orderId, requestTopic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
            } else {
                log.error("Failed to publish {} message {} to topic {}", requestModelName, requestModel.toString(), requestTopic, ex);
            }
        };

    }
}