package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.PaymentInfoResult;
import com.rbkmoney.swag.fraudbusters.management.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PaymentInfoResultToPaymentsConverter implements Converter<PaymentInfoResult, List<Payment>> {

    private final PaymentInfoToPaymentConverter paymentInfoToPaymentConverter;

    @Override
    public List<Payment> convert(PaymentInfoResult payments) {
        return payments.getPayments().stream()
                .map(paymentInfoToPaymentConverter::convert)
                .collect(Collectors.toList());
    }
}
