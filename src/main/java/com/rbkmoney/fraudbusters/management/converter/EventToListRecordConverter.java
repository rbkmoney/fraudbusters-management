package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class EventToListRecordConverter implements Converter<Event, WbListRecords> {

    @Override
    public WbListRecords convert(Event event) {
        WbListRecords record = new WbListRecords();
        Row row = event.getRow();
        UUID uuid = UUID.randomUUID();
        record.setId(uuid.toString());
        record.setPartyId(row.getPartyId());
        record.setShopId(row.getShopId());
        record.setListName(row.getListName());
        record.setValue(row.getValue());
        return record;
    }
}
