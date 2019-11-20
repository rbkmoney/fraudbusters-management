package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.rbkmoney.fraudbusters.management.domain.CountInfoListRequest;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.utils.CountInfoListRequestGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class P2pWbListRecordsToListRecordWithRowConverter implements Converter<P2pWbListRecords, CountInfoListRequest> {

    private final CountInfoListRequestGenerator countInfoListRequestGenerator;

    public CountInfoListRequest convert(P2pWbListRecords destination) {
        P2pListRecord listRecord = new P2pListRecord();
        listRecord.setValue(destination.getValue());
        listRecord.setIdentityId(destination.getIdentityId());
        return countInfoListRequestGenerator.initDestination(destination.getRowInfo(), listRecord);
    }
}
