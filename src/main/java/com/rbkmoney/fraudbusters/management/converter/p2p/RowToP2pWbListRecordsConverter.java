package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.damsel.wb_list.P2pId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import com.rbkmoney.fraudbusters.management.utils.RowUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RowToP2pWbListRecordsConverter implements Converter<Row, P2pWbListRecords> {

    private final RowUtilsService rowUtilsService;

    @Override
    public P2pWbListRecords convert(Row destination) {
        P2pWbListRecords wbListRecords = new P2pWbListRecords();

        if (destination.isSetId() && destination.getId().isSetP2pId()) {
            P2pId p2pId = destination.getId().getP2pId();
            wbListRecords.setIdentityId(p2pId.getIdentityId());
            wbListRecords.setListName(destination.getListName());
            wbListRecords.setListType(rowUtilsService.initListType(destination));
            wbListRecords.setValue(destination.getValue());
            wbListRecords.setRowInfo(rowUtilsService.initRowInfo(destination));
            return wbListRecords;
        }

        throw new UnknownEventException();
    }

}
