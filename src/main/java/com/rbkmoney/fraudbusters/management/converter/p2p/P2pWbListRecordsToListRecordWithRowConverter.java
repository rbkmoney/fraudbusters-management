package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.utils.P2pCountInfoListRequestGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class P2pWbListRecordsToListRecordWithRowConverter implements Converter<P2pWbListRecords, P2pCountInfo> {

    private final P2pCountInfoListRequestGenerator countInfoListRequestGenerator;

    public P2pCountInfo convert(P2pWbListRecords destination) {
        P2pListRecord listRecord = new P2pListRecord();
        listRecord.setValue(destination.getValue());
        listRecord.setIdentityId(destination.getIdentityId());
        return countInfoListRequestGenerator.initDestination(destination.getRowInfo(), listRecord);
    }
}
