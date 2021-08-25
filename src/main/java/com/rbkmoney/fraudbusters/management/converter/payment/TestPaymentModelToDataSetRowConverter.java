package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSetRow;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestPaymentModelToDataSetRowConverter implements Converter<TestPaymentModel, DataSetRow> {

    private final TestPaymentModelToPaymentApiConverter testPaymentModelToPaymentApiConverter;

    @Override
    public DataSetRow convert(TestPaymentModel testPaymentModel) {
        return new DataSetRow()
                .id(String.valueOf(testPaymentModel.getTestDataSetId()))
                .payment(testPaymentModelToPaymentApiConverter.convert(testPaymentModel));
    }

}
