package com.food.ordering.common.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ErrorDto {
    private final String code;
    private final List<String> messages;
}
