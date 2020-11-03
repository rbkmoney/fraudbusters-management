package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pCountInfoListRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class P2pCountInfoGenerator {

    private final CountInfoUtils countInfoGenerator;
    private final P2pListRecordToRowConverter listRecordToRowConverter;
    private final P2pCountInfoListRequestToRowConverter countInfoListRecordToRowConverter;

    public P2pCountInfo initDestination(String rowInfo, P2pListRecord listRecord) {
        P2pCountInfo p2PCountInfo = new P2pCountInfo();
        p2PCountInfo.setListRecord(listRecord);
        if (!StringUtil.isNullOrEmpty(rowInfo)) {
            CountInfo countInfoValue = countInfoGenerator.initRowCountInfo(rowInfo);
            p2PCountInfo.setCountInfo(countInfoValue);
        }
        return p2PCountInfo;
    }

    public Row initRow(P2pCountInfo record, com.rbkmoney.damsel.wb_list.ListType listType) {
        Row row = null;
        switch (listType) {
            case black:
            case white:
                row = listRecordToRowConverter.convert(record.getListRecord());
                break;
            case grey:
                row = countInfoListRecordToRowConverter.convert(record);
                break;
            default:
                throw new UnknownEventException();
        }
        return row;
    }

}
