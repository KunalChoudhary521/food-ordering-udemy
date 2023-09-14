package com.food.ordering.payment.data.access.credithistory.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.data.access.credithistory.entity.CreditHistoryEntity;
import com.food.ordering.payment.domain.entity.CreditHistory;
import com.food.ordering.payment.domain.valueobject.CreditHistoryId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CreditHistoryDataMapper extends MoneyMapper {

    @Mapping(target = "id", source = "creditHistory.id.value")
    @Mapping(target = "customerId", source = "creditHistory.customerId.value")
    @Mapping(target = "type", source = "creditHistory.transactionType")
    CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory);

    @Mapping(target = "creditHistoryId", source = "creditHistoryEntity.id")
    @Mapping(target = "transactionType", source = "creditHistoryEntity.type")
    CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity);

    List<CreditHistory> creditHistoryEntitiesToCreditHistories(List<CreditHistoryEntity> creditHistoryEntity);

    CustomerId toCustomerId(UUID value); // TODO: move to common mapper class/interface

    CreditHistoryId toCreditHistoryId(UUID value);
}
