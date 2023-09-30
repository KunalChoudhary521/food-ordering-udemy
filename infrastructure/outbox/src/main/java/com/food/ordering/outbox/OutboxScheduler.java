package com.food.ordering.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
