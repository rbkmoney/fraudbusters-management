package com.rbkmoney.fraudbusters.management.converter.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListToListConverter {

    public <T, R> List<R> convert(List<T> chargebacks, Converter<T, R> converter) {
        return chargebacks.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

}
