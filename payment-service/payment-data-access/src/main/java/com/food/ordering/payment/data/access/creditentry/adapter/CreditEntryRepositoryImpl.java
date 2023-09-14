package com.food.ordering.payment.data.access.creditentry.adapter;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.data.access.creditentry.entity.CreditEntryEntity;
import com.food.ordering.payment.data.access.creditentry.mapper.CreditEntryDataMapper;
import com.food.ordering.payment.data.access.creditentry.repository.CreditEntryJpaRepository;
import com.food.ordering.payment.domain.entity.CreditEntry;
import com.food.ordering.payment.domain.port.output.repository.CreditEntryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataMapper creditEntryDataMapper;

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        CreditEntryEntity creditEntryEntity = creditEntryDataMapper.creditEntryToCreditEntryEntity(creditEntry);
        CreditEntryEntity savedCreditEntryEntity = creditEntryJpaRepository.save(creditEntryEntity);
        return creditEntryDataMapper.creditEntryEntityToCreditEntry(savedCreditEntryEntity);
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository
                .findByCustomerId(customerId.getValue())
                .map(creditEntryDataMapper::creditEntryEntityToCreditEntry);
    }
}
