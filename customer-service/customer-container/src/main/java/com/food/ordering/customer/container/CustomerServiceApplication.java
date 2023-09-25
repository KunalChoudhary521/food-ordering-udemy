package com.food.ordering.customer.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.food.ordering.customer.data.access")
@EntityScan(basePackages = "com.food.ordering.customer.data.access")
@SpringBootApplication(scanBasePackages = "com.food.ordering")
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class);
    }
}
