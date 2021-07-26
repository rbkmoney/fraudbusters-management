package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.swag.fraudbusters.management.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentInfoResultToPaymentsConverter
        implements Converter<List<com.rbkmoney.damsel.fraudbusters.Payment>, List<Payment>> {

    private final PaymentInfoToPaymentConverter paymentInfoToPaymentConverter;

    @Override
    public List<Payment> convert(List<com.rbkmoney.damsel.fraudbusters.Payment> payments) {
        return payments.stream()
                .map(paymentInfoToPaymentConverter::convert)
                .collect(Collectors.toList());
    }
}
