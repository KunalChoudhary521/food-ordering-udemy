package com.food.ordering.order.data.access.customer.adapter;

import com.food.ordering.order.data.access.customer.mapper.CustomerEntityMapper;
import com.food.ordering.order.data.access.customer.respository.CustomerJpaRepository;
import com.food.ordering.order.domain.entity.Customer;
import com.food.ordering.order.domain.port.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerEntityMapper customerEntityMapper;

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerEntityMapper::customerEntityToCustomer);
    }
}
