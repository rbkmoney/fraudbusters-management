package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.damsel.wb_list.IdInfo;
import com.rbkmoney.damsel.wb_list.PaymentId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.utils.CountInfoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCountInfoRequestToRowConverter implements Converter<PaymentCountInfo, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;
    private final CountInfoUtils countInfoUtils;

    @Override
    public Row convert(PaymentCountInfo destination) {
        Row row = listRecordToRowConverter.destinationToSource(destination.getListRecord());
        initIdInfo(destination, row);
        countInfoUtils.initRowCountInfo(destination.getCountInfo(), row);
        return row;
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
