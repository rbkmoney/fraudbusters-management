package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentCountInfo;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCountInfoRequestToRowConverter implements Converter<PaymentCountInfo, Row> {

    private final PaymentListRecordToRowConverter paymentListRecordToRowConverter;
    private final CountInfoUtils countInfoUtils;

    @Override
    public Row convert(PaymentCountInfo destination) {
        PaymentListRecord listRecord = destination.getListRecord();
        return paymentListRecordToRowConverter.convert(listRecord)
                .setRowInfo(countInfoUtils.initRowInfo(destination.getCountInfo()));
    }

}
