package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.CheckedPaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.CheckedDataSetRow;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckedPaymentModelToCheckedDataSetRowConverter
        implements Converter<CheckedPaymentModel, CheckedDataSetRow> {

    private final PaymentModelToPaymentApiConverter paymentModelToPaymentApiConverter;

    @Override
    public CheckedDataSetRow convert(CheckedPaymentModel testDataSetModel) {
        var testPaymentModel = testDataSetModel.getPaymentModel();
        return new CheckedDataSetRow()
                .id(String.valueOf(testDataSetModel.getTestDataSetCheckingResultId()))
                .resultStatus(testDataSetModel.getResultStatus())
                .ruleChecked(testDataSetModel.getRuleChecked())
                .checkedTemplate(testDataSetModel.getCheckedTemplate())
                .notificationRule(testDataSetModel.getNotificationRule())
                .payment(paymentModelToPaymentApiConverter.convert(testPaymentModel));
    }

}
