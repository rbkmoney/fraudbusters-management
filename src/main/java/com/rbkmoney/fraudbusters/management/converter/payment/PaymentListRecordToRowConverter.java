package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.PaymentId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.PaymentListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentListRecordToRowConverter implements Converter<PaymentListRecord, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;

    @Override
    public Row convert(PaymentListRecord source) {
        Row row = listRecordToRowConverter.destinationToSource(source);

        row.setId(IdInfo.payment_id(new PaymentId()
                .setPartyId(source.getPartyId())
                .setShopId(source.getShopId()))
        );
        return row;
    }
}
