package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.P2pId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class P2pCountInfoListRequestToRowConverter implements Converter<P2pCountInfo, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;
    private final CountInfoUtils countInfoUtils;

    @Override
    public Row convert(P2pCountInfo p2PCountInfo) {
        Row row = listRecordToRowConverter.destinationToSource(p2PCountInfo.getListRecord());
        initIdInfo(p2PCountInfo, row);
        countInfoUtils.initRowCountInfo(p2PCountInfo.getCountInfo(), row);
        return row;
    }

    private Row initIdInfo(P2pCountInfo destination, Row row) {
        P2pListRecord listRecord = destination.getListRecord();
        row.setId(IdInfo.p2p_id(new P2pId()
                .setIdentityId(listRecord.getIdentityId()))
        );
        return row;
    }
}
