package com.food.ordering.payment.container;

import com.food.ordering.payment.domain.PaymentDomainService;
import com.food.ordering.payment.domain.PaymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentApplicationConfig {

    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }
}
