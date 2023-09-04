package com.food.ordering.order.application.exception;

import com.food.ordering.common.application.ErrorDto;
import com.food.ordering.common.application.GlobalExceptionHandler;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
@Slf4j
public class OrderExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler({OrderDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(OrderDomainException orderDomainException) {
        log.error(orderDomainException.getMessage(), orderDomainException);
        return ErrorDto.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(List.of(orderDomainException.getMessage()))
                .build();
    }

    @ResponseBody
    @ExceptionHandler({OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(OrderNotFoundException orderNotFoundException) {
        log.error(orderNotFoundException.getMessage(), orderNotFoundException);
        return ErrorDto.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .messages(List.of(orderNotFoundException.getMessage()))
                .build();
    }
}
