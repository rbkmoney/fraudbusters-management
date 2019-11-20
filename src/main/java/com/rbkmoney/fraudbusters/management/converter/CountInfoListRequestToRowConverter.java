package com.rbkmoney.fraudbusters.management.converter;


import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.management.domain.CountInfoListRequest;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CountInfoListRequestToRowConverter implements Converter<CountInfoListRequest, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;

    @Override
    public Row convert(CountInfoListRequest destination) {
        Row row = listRecordToRowConverter.destinationToSource(destination.getListRecord());

        initIdInfo(destination, row);

        String startCountTime = StringUtil.isNullOrEmpty(destination.getStartCountTime()) ?
                Instant.now().toString() : destination.getStartCountTime();
        row.setRowInfo(RowInfo.count_info(new CountInfo()
                .setCount(destination.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(destination.getEndCountTime())));
        return row;
    }

    private Row initIdInfo(CountInfoListRequest destination, Row row) {
        ListRecord listRecord = destination.getListRecord();

        if (listRecord instanceof PaymentListRecord) {
            PaymentListRecord paymentListRecord = (PaymentListRecord) listRecord;
            row.setId(IdInfo.payment_id(new PaymentId()
                    .setPartyId(paymentListRecord.getPartyId())
                    .setShopId(paymentListRecord.getShopId()))
            );
        } else if (listRecord instanceof P2pListRecord) {
            P2pListRecord p2pListRecord = (P2pListRecord) listRecord;
            row.setId(IdInfo.p2p_id(new P2pId()
                    .setIdentityId(p2pListRecord.getIdentityId()))
            );
        } else {
            throw new UnknownEventException("Unknown listRecord!");
        }

        return row;
    }
}
