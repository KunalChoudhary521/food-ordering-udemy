package com.food.ordering.payment.domain.port.output.repository;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);
    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
