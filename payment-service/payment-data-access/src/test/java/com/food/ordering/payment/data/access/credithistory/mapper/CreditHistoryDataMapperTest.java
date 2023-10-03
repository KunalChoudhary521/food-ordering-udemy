package com.food.ordering.payment.data.access.credithistory.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.payment.data.access.credithistory.entity.CreditHistoryEntity;
import com.food.ordering.payment.domain.entity.CreditHistory;
import com.food.ordering.payment.domain.valueobject.CreditHistoryId;
import com.food.ordering.payment.domain.valueobject.TransactionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditHistoryDataMapperTest {

    private static final CreditHistoryId TEST_CREDIT_HISTORY_ID_1 = new CreditHistoryId(UUID.fromString("ac5a6118-177a-4bb6-a85f-bd15bf370d40"));
    private static final CreditHistoryId TEST_CREDIT_HISTORY_ID_2 = new CreditHistoryId(UUID.fromString("2ed6ffc0-87a0-4d02-bbc3-7ec7b33cb96a"));
    private static final CustomerId TEST_CUSTOMER_ID_1 = new CustomerId(UUID.fromString("ccd716e5-6044-4dbb-931a-698565de2a40"));
    private static final CustomerId TEST_CUSTOMER_ID_2 = new CustomerId(UUID.fromString("7342d6cc-f360-4618-bd0b-7e989fb59e7f"));
    private static final Money TEST_PRICE_1 = new Money(new BigDecimal("10.00"));
    private static final Money TEST_PRICE_2 = new Money(new BigDecimal("15.00"));

    private final CreditHistoryDataMapper creditHistoryDataMapper = Mappers.getMapper(CreditHistoryDataMapper.class);

    @Test
    void creditHistories_creditHistoryEntitiesToCreditHistories_creditHistoryEntities() {
        CreditHistoryEntity creditHistoryEntity1 = CreditHistoryEntity.builder()
                .id(TEST_CREDIT_HISTORY_ID_1.getValue())
                .customerId(TEST_CUSTOMER_ID_1.getValue())
                .amount(TEST_PRICE_1.getAmount())
                .type(TransactionType.DEBIT)
                .build();

        CreditHistoryEntity creditHistoryEntity2 = CreditHistoryEntity.builder()
                .id(TEST_CREDIT_HISTORY_ID_2.getValue())
                .customerId(TEST_CUSTOMER_ID_2.getValue())
                .amount(TEST_PRICE_2.getAmount())
                .type(TransactionType.CREDIT)
                .build();

        List<CreditHistoryEntity> creditHistoryEntities = List.of(creditHistoryEntity1, creditHistoryEntity2);
        List<CreditHistory> creditHistories = creditHistoryDataMapper.creditHistoryEntitiesToCreditHistories(creditHistoryEntities);

        assertEquals(creditHistoryEntity1.getId(), creditHistories.get(0).getId().getValue());
        assertEquals(creditHistoryEntity1.getCustomerId(), creditHistories.get(0).getCustomerId().getValue());
        assertEquals(creditHistoryEntity1.getAmount(), creditHistories.get(0).getAmount().getAmount());
        assertEquals(creditHistoryEntity1.getType(), creditHistories.get(0).getTransactionType());

        assertEquals(creditHistoryEntity2.getId(), creditHistories.get(1).getId().getValue());
        assertEquals(creditHistoryEntity2.getCustomerId(), creditHistories.get(1).getCustomerId().getValue());
        assertEquals(creditHistoryEntity2.getAmount(), creditHistories.get(1).getAmount().getAmount());
        assertEquals(creditHistoryEntity2.getType(), creditHistories.get(1).getTransactionType());
    }

    @Test
    void creditHistoryEntity_creditHistoryEntityToCreditHistory_creditHistory() {
        CreditHistory creditHistory = CreditHistory.builder()
                .creditHistoryId(TEST_CREDIT_HISTORY_ID_1)
                .customerId(TEST_CUSTOMER_ID_1)
                .amount(TEST_PRICE_1)
                .transactionType(TransactionType.CREDIT)
                .build();

        CreditHistoryEntity creditHistoryEntity = creditHistoryDataMapper.creditHistoryToCreditHistoryEntity(creditHistory);

        assertEquals(creditHistory.getId().getValue(), creditHistoryEntity.getId());
        assertEquals(creditHistory.getCustomerId().getValue(), creditHistoryEntity.getCustomerId());
        assertEquals(creditHistory.getAmount().getAmount(), creditHistoryEntity.getAmount());
        assertEquals(creditHistory.getTransactionType(), creditHistoryEntity.getType());
    }
}