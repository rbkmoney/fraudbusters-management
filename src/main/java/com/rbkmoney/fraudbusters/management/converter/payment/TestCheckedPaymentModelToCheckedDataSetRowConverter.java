package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.CheckedDataSetRow;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestCheckedPaymentModelToCheckedDataSetRowConverter
        implements Converter<TestCheckedPaymentModel, CheckedDataSetRow> {

    private final TestPaymentModelToPaymentApiConverter testPaymentModelToPaymentApiConverter;

    @Override
    public CheckedDataSetRow convert(TestCheckedPaymentModel testDataSetModel) {
        var testPaymentModel = testDataSetModel.getTestPaymentModel();
        return new CheckedDataSetRow()
                .id(String.valueOf(testDataSetModel.getTestDataSetCheckingResultId()))
                .resultStatus(testDataSetModel.getResultStatus())
                .ruleChecked(testDataSetModel.getRuleChecked())
                .checkedTemplate(testDataSetModel.getCheckedTemplate())
                .notificationRule(testDataSetModel.getNotificationRule())
                .payment(testPaymentModelToPaymentApiConverter.convert(testPaymentModel));
    }

}
