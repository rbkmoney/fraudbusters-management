package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.P2pId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class P2pCountInfoListRequestToRowConverter implements Converter<P2pCountInfo, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;

    @Override
    public Row convert(P2pCountInfo p2PCountInfo) {
        Row row = listRecordToRowConverter.destinationToSource(p2PCountInfo.getListRecord());
        initIdInfo(p2PCountInfo, row);
        CountInfo countInfo = p2PCountInfo.getCountInfo();
        String startCountTime = StringUtil.isNullOrEmpty(countInfo.getStartCountTime()) ?
                Instant.now().toString() : countInfo.getStartCountTime();
        row.setRowInfo(RowInfo.count_info(new com.rbkmoney.damsel.wb_list.CountInfo()
                .setCount(countInfo.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(countInfo.getEndCountTime())));
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
