package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.P2pId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class P2pListRecordToRowConverter implements Converter<P2pListRecord, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;

    @Override
    public Row convert(P2pListRecord source) {
        Row row = listRecordToRowConverter.destinationToSource(source);

        row.setId(IdInfo.p2p_id(new P2pId()
                .setIdentityId(source.getIdentityId()))
        );
        return row;
    }
}
