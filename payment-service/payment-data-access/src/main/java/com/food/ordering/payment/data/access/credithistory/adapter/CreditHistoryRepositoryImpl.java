package com.food.ordering.payment.data.access.credithistory.adapter;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.data.access.credithistory.entity.CreditHistoryEntity;
import com.food.ordering.payment.data.access.credithistory.mapper.CreditHistoryDataMapper;
import com.food.ordering.payment.data.access.credithistory.repository.CreditHistoryJpaRepository;
import com.food.ordering.payment.domain.entity.CreditHistory;
import com.food.ordering.payment.domain.port.output.repository.CreditHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataMapper creditHistoryDataMapper;

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataMapper.creditHistoryEntityToCreditHistory(creditHistoryJpaRepository
                .save(creditHistoryDataMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        Optional<List<CreditHistoryEntity>> creditHistories = creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
        return creditHistories.map(creditHistoryDataMapper::creditHistoryEntitiesToCreditHistories);
    }
}
