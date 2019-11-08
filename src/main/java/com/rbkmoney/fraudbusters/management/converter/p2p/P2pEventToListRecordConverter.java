package com.rbkmoney.fraudbusters.management.converter.p2p;

import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class P2pEventToListRecordConverter implements Converter<Event, P2pWbListRecords> {

    private final RowToP2pWbListRecordsConverter rowToWbListRecordsConverter;

    @Override
    public P2pWbListRecords convert(Event event) {
        P2pWbListRecords record = rowToWbListRecordsConverter.convert(event.getRow());
        record.setId(UUID.randomUUID().toString());
        return record;
    }
}
