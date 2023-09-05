package com.food.ordering.order.data.access.customer.mapper;

import com.food.ordering.order.data.access.customer.entity.CustomerEntity;
import com.food.ordering.order.domain.entity.Customer;
import com.food.ordering.order.domain.mapper.BaseIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerEntityMapper extends BaseIdMapper {

    Customer customerEntityToCustomer(CustomerEntity customerEntity);
}
