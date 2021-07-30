package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.swag.fraudbusters.management.model.InspectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FraudResultToInspectResultConverter
        implements Converter<com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck, InspectResult> {

    private final PaymentInfoToPaymentConverter paymentInfoToPaymentConverter;

    @NonNull
    @Override
    public InspectResult convert(com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck transactionCheck) {
        var checkResult = transactionCheck.getCheckResult();
        return new InspectResult()
                .payment(paymentInfoToPaymentConverter.convert(transactionCheck.getTransaction()))
                .checkedTemplate(checkResult.getCheckedTemplate())
                .resultStatus(checkResult.getConcreteCheckResult().getResultStatus().toString())
                .notificationsRule(checkResult.getConcreteCheckResult().getNotificationsRule())
                .ruleChecked(checkResult.getConcreteCheckResult().getRuleChecked());
    }
}
