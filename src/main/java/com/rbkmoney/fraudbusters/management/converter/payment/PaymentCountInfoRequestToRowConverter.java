package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.PaymentId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentCountInfoRequestToRowConverter implements Converter<PaymentCountInfo, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;

    @Override
    public Row convert(PaymentCountInfo destination) {
        Row row = listRecordToRowConverter.destinationToSource(destination.getListRecord());
        initIdInfo(destination, row);
        initCountInfo(destination, row);
        return row;
    }

    private void initCountInfo(PaymentCountInfo destination, Row row) {
        CountInfo countInfo = destination.getCountInfo();
        String startCountTime = StringUtil.isNullOrEmpty(countInfo.getStartCountTime()) ?
                Instant.now().toString() : countInfo.getStartCountTime();
        row.setRowInfo(RowInfo.count_info(new com.rbkmoney.damsel.wb_list.CountInfo()
                .setCount(countInfo.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(countInfo.getEndCountTime())));
    }

    private Row initIdInfo(PaymentCountInfo destination, Row row) {
        PaymentListRecord listRecord = destination.getListRecord();
        row.setId(IdInfo.payment_id(new PaymentId()
                .setPartyId(listRecord.getPartyId())
                .setShopId(listRecord.getShopId()))
        );
        return row;
    }
}
