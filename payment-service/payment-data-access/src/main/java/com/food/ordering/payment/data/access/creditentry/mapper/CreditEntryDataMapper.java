package com.food.ordering.payment.data.access.creditentry.mapper;

import com.food.ordering.domain.mapper.MoneyMapper;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.data.access.creditentry.entity.CreditEntryEntity;
import com.food.ordering.payment.domain.entity.CreditEntry;
import com.food.ordering.payment.domain.valueobject.CreditEntryId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CreditEntryDataMapper extends MoneyMapper {

    @Mapping(target = "id", source = "creditEntry.id.value")
    @Mapping(target = "customerId", source = "creditEntry.customerId.value")
    CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry);

    @Mapping(target = "creditEntryId", source = "creditEntryEntity.id")
    CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity);

    CreditEntryId toCreditEntryId(UUID value); // TODO: move to common mapper class/interface

    CustomerId toCustomerId(UUID value);
}
