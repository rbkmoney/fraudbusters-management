package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.PaymentId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentListRecordToRowConverter implements Converter<PaymentListRecord, Row> {

    @NonNull
    @Override
    public Row convert(PaymentListRecord source) {
        return new Row().setId(IdInfo.payment_id(new PaymentId()
                .setPartyId(source.getPartyId())
                .setShopId(source.getShopId())))
                .setListName(source.getListName())
                .setValue(source.getValue());
    }
}
