package com.food.ordering.saga;

import com.food.ordering.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, U extends DomainEvent> {

    S process(T data);
    U rollback(T data);
}
