package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ListRecordToRowConverter implements Converter<ListRecord, Row> {

    @Override
    public Row convert(ListRecord listRecord) {
        Row row = new Row();
        row.setPartyId(listRecord.getPartyId());
        row.setShopId(listRecord.getShopId());
        row.setListName(listRecord.getListName());
        row.setValue(listRecord.getValue());
        return row;
    }
}
