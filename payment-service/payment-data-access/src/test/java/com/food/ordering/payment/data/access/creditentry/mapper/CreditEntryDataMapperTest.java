package com.food.ordering.payment.data.access.creditentry.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.payment.data.access.creditentry.entity.CreditEntryEntity;
import com.food.ordering.payment.domain.entity.CreditEntry;
import com.food.ordering.payment.domain.valueobject.CreditEntryId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditEntryDataMapperTest {

    private final static CreditEntryId TEST_CREDIT_ENTRY_ID = new CreditEntryId(UUID.fromString("470fee57-076d-4674-976a-47677e2e1e1c"));
    private final static CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("e83b984d-9924-4e3b-aead-393099d94db2"));
    private final static Money TEST_PRICE = new Money(new BigDecimal("10.27"));

    private final CreditEntryDataMapper creditEntryDataMapper = Mappers.getMapper(CreditEntryDataMapper.class);

    @Test
    void creditEntry_creditEntryToCreditEntryEntity_creditEntryEntity() {
        CreditEntry creditEntry = CreditEntry.builder()
                .creditEntryId(TEST_CREDIT_ENTRY_ID)
                .customerId(TEST_CUSTOMER_ID)
                .totalCreditAmount(TEST_PRICE)
                .build();

        CreditEntryEntity creditEntryEntity = creditEntryDataMapper.creditEntryToCreditEntryEntity(creditEntry);

        assertEquals(creditEntry.getId().getValue(), creditEntryEntity.getId());
        assertEquals(creditEntry.getCustomerId().getValue(), creditEntryEntity.getCustomerId());
        assertEquals(creditEntry.getTotalCreditAmount().getAmount(), creditEntryEntity.getTotalCreditAmount());
    }

    @Test
    void creditEntryEntity_creditEntryEntityToCreditEntry_creditEntry() {
        CreditEntryEntity creditEntryEntity = CreditEntryEntity.builder()
                .id(TEST_CREDIT_ENTRY_ID.getValue())
                .customerId(TEST_CUSTOMER_ID.getValue())
                .totalCreditAmount(TEST_PRICE.getAmount())
                .build();

        CreditEntry creditEntry = creditEntryDataMapper.creditEntryEntityToCreditEntry(creditEntryEntity);

        assertEquals(creditEntryEntity.getId(), creditEntry.getId().getValue());
        assertEquals(creditEntryEntity.getCustomerId(), creditEntry.getCustomerId().getValue());
        assertEquals(creditEntryEntity.getTotalCreditAmount(), creditEntry.getTotalCreditAmount().getAmount());
    }
}