package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import com.rbkmoney.swag.fraudbusters.management.model.DataSetRow;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentModelToDataSetRowConverter implements Converter<PaymentModel, DataSetRow> {

    private final PaymentModelToPaymentApiConverter paymentModelToPaymentApiConverter;

    @Override
    public DataSetRow convert(PaymentModel paymentModel) {
        return new DataSetRow()
                .id(String.valueOf(paymentModel.getTestDataSetId()))
                .payment(paymentModelToPaymentApiConverter.convert(paymentModel));
    }

}
