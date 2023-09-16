package com.food.ordering.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@Slf4j
public class KafkaMessagePublisher {

    public <T> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(String topic, T avroModel,
                                                                             String orderId, String avroModelName) {
        return (result, ex) -> {
            if (ex == null) {
                log.info("Successfully received response from Kafka for order id {} Topic: {} Partition: {} Offset {} Timestamp {}",
                        orderId, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
            } else {
                log.error("Failed to publish {} message {} to topic {}", avroModelName, avroModel.toString(), topic, ex);
            }
        };

    }
}
