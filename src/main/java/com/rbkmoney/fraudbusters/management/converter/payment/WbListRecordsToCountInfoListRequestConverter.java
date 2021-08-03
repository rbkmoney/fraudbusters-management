package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentCountInfo;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WbListRecordsToCountInfoListRequestConverter implements Converter<WbListRecords, PaymentCountInfo> {

    private final PaymentCountInfoGenerator countInfoListRequestGenerator;

    public PaymentCountInfo convert(WbListRecords destination) {
        var listRecord = new PaymentListRecord()
                .value(destination.getValue())
                .partyId(destination.getPartyId())
                .shopId(destination.getShopId());
        return countInfoListRequestGenerator.initDestination(destination.getRowInfo(), listRecord);
    }

}
