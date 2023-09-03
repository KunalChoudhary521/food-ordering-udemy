package com.food.ordering.kafka.producer.service;

import com.food.ordering.kafka.producer.exception.KafkaProducerException;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    @Override
    public void send(String topicName, K key, V message, BiConsumer<SendResult<K, V>, Throwable> callback) {
        log.info("Publishing message with id: {} to topic: {}", key, topicName);
        try {
            CompletableFuture<SendResult<K, V>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
            kafkaResultFuture.whenComplete(callback);
        } catch (Exception ex) {
            log.error("Error when publishing message with id: {} to topic: {}", key, topicName);
            throw new KafkaProducerException(ex.getMessage());
        }
    }

    @PreDestroy
    public void close() {
        if(kafkaTemplate != null) {
            log.info("Closing Kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
