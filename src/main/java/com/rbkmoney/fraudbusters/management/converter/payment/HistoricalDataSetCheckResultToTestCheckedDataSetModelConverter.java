package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataSetCheckResult;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedPaymentModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter
        implements Converter<HistoricalDataSetCheckResult, CheckedDataSetModel> {

    private final PaymentToTestPaymentModelConverter paymentToTestPaymentModelConverter;

    @NonNull
    @Override
    public CheckedDataSetModel convert(HistoricalDataSetCheckResult historicalDataSetCheckResult) {
        var testCheckedDataSetModel = new CheckedDataSetModel();
        testCheckedDataSetModel.setCheckedPaymentModels(historicalDataSetCheckResult
                .getHistoricalTransactionCheck().stream()
                .map(this::mapToCheckedPaymentModel)
                .collect(Collectors.toList()));
        return testCheckedDataSetModel;
    }

    private CheckedPaymentModel mapToCheckedPaymentModel(
            com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck historicalTransactionCheck) {
        return CheckedPaymentModel.builder()
                .testPaymentId(Long.valueOf(historicalTransactionCheck.getTransaction().getId()))
                .checkedTemplate(historicalTransactionCheck.getCheckResult().getCheckedTemplate())
                .resultStatus(historicalTransactionCheck.getCheckResult()
                        .getConcreteCheckResult().getResultStatus().getSetField().getFieldName())
                .ruleChecked(
                        historicalTransactionCheck.getCheckResult().getConcreteCheckResult().getRuleChecked())
                .notificationRule(historicalTransactionCheck.getCheckResult().getConcreteCheckResult()
                        .getNotificationsRule())
                .paymentModel(
                        paymentToTestPaymentModelConverter.convert(historicalTransactionCheck.getTransaction()))
                .build();
    }

}
