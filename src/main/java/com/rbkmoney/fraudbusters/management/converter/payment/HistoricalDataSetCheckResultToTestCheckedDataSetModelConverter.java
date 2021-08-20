package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataSetCheckResult;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter
        implements Converter<HistoricalDataSetCheckResult, TestCheckedDataSetModel> {

    private final PaymentToTestPaymentModelConverter paymentToTestPaymentModelConverter;

    @NonNull
    @Override
    public TestCheckedDataSetModel convert(HistoricalDataSetCheckResult historicalDataSetCheckResult) {
        var testCheckedDataSetModel = new TestCheckedDataSetModel();
        testCheckedDataSetModel.setTestCheckedPaymentModels(historicalDataSetCheckResult
                .getHistoricalTransactionCheck().stream()
                .map(historicalTransactionCheck -> TestCheckedPaymentModel.builder()
                        .testPaymentId(historicalTransactionCheck.getTransaction().getId())
                        .checkedTemplate(historicalTransactionCheck.getCheckResult().getCheckedTemplate())
                        .resultStatus(
                                historicalTransactionCheck.getCheckResult().getConcreteCheckResult().getResultStatus()
                                        .toString())
                        .ruleChecked(
                                historicalTransactionCheck.getCheckResult().getConcreteCheckResult().getRuleChecked())
                        .notificationRule(historicalTransactionCheck.getCheckResult().getConcreteCheckResult()
                                .getNotificationsRule())
                        .testPaymentModel(
                                paymentToTestPaymentModelConverter.convert(historicalTransactionCheck.getTransaction()))
                        .build())
                .collect(Collectors.toList()));
        return testCheckedDataSetModel;
    }

}
