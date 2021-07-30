package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.swag.fraudbusters.management.model.FraudPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FraudPaymentInfoToFraudPaymentConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.FraudPaymentInfo, FraudPayment> {

    private final PaymentInfoToPaymentConverter paymentInfoToPaymentConverter;

    @NonNull
    @Override
    public FraudPayment convert(com.rbkmoney.damsel.fraudbusters.FraudPaymentInfo fraudPayment) {
        return new FraudPayment()
                .payment(paymentInfoToPaymentConverter.convert(fraudPayment.getPayment()))
                .comment(fraudPayment.getComment())
                .type(fraudPayment.getType());
    }
}
