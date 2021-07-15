package com.rbkmoney.fraudbusters.management.utils;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static LocalDateTime toDate(@RequestParam @Validated String to) {
        return LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME);
    }

}
