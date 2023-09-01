package com.food.ordering.domain.event;

public interface DomainEventPublisher<T extends DomainEvent<?>> {

    void publish(T domainEvent);
}
