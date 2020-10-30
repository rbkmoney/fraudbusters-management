package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.PaymentId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WbListRecordToRowConverter implements Converter<WbListRecords, Row> {

    @Override
    public Row convert(WbListRecords source) {
        Row row = new Row();
        row.setListName(source.getListName());
        row.setValue(source.getValue());
        row.setId(IdInfo.payment_id(new PaymentId()
                .setPartyId(source.getPartyId())
                .setShopId(source.getShopId()))
        );
        return row;
    }
}
