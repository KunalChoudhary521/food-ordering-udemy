package com.food.ordering.domain.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

    public static final ZonedDateTime CURRENT_UTC_TIME = ZonedDateTime.now(ZoneOffset.UTC);
}
