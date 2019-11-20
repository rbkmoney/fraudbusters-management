package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.fraudbusters.management.domain.CountInfoListRequest;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.utils.CountInfoListRequestGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WbListRecordsToCountInfoListRequestConverter implements Converter<WbListRecords, CountInfoListRequest> {

    private final CountInfoListRequestGenerator countInfoListRequestGenerator;

    public CountInfoListRequest convert(WbListRecords destination) {
        PaymentListRecord listRecord = new PaymentListRecord();
        listRecord.setValue(destination.getValue());
        listRecord.setPartyId(destination.getPartyId());
        listRecord.setShopId(destination.getShopId());
        return countInfoListRequestGenerator.initDestination(destination.getRowInfo(), listRecord);
    }

}
