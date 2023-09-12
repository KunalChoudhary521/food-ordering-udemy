package com.food.ordering.payment.domain.port.output.repository;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);
    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
