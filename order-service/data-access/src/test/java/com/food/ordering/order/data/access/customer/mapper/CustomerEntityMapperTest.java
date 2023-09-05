package com.food.ordering.order.data.access.customer.mapper;

import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.order.data.access.customer.entity.CustomerEntity;
import com.food.ordering.order.domain.entity.Customer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerEntityMapperTest {

    private static final CustomerId TEST_CUSTOMER_ID = new CustomerId(UUID.fromString("4a046f3e-693e-465e-9989-2bdad7c4a445"));

    private final CustomerEntityMapper customerEntityMapper = Mappers.getMapper(CustomerEntityMapper.class);

    @Test
    void customerEntity_customerEntityToCustomer_customer() {
        CustomerEntity customerEntity = CustomerEntity.builder().id(TEST_CUSTOMER_ID.getValue()).build();

        Customer customer = customerEntityMapper.customerEntityToCustomer(customerEntity);

        assertEquals(customerEntity.getId(), customer.getId().getValue());
    }
}