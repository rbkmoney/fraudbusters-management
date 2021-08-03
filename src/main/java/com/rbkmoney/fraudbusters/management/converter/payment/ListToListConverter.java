package com.rbkmoney.fraudbusters.management.converter.payment;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListToListConverter {

    public <T, R> List<R> convert(List<T> inList, Converter<T, R> converter) {
        return inList.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

}
