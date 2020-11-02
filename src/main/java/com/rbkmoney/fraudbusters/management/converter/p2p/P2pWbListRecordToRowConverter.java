package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.P2pId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class P2pWbListRecordToRowConverter implements Converter<P2pWbListRecords, Row> {

    @Override
    public Row convert(P2pWbListRecords source) {
        Row row = new Row();
        row.setListName(source.getListName());
        row.setValue(source.getValue());
        row.setId(IdInfo.p2p_id(new P2pId()
                .setIdentityId(source.getIdentityId()))
        );
        return row;
    }
}
