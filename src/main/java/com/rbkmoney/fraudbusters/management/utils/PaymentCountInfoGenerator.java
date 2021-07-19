package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentCountInfoRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentCountInfo;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentListRecord;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCountInfoGenerator {

    private final CountInfoSwagUtils countInfoSwagUtils;
    private final PaymentListRecordToRowConverter paymentListRecordToRowConverter;
    private final PaymentCountInfoRequestToRowConverter countInfoListRecordToRowConverter;

    public PaymentCountInfo initDestination(String rowInfo, PaymentListRecord listRecord) {
        var paymentCountInfo = new PaymentCountInfo();
        paymentCountInfo.setListRecord(listRecord);
        if (!StringUtil.isNullOrEmpty(rowInfo)) {
            com.rbkmoney.swag.fraudbusters.management.model.CountInfo countInfoValue =
                    countInfoSwagUtils.initExternalRowCountInfo(rowInfo);
            paymentCountInfo.setCountInfo(countInfoValue);
        }
        return paymentCountInfo;
    }

    public Row initRow(PaymentCountInfo record, com.rbkmoney.damsel.wb_list.ListType listType) {
        Row row = null;
        switch (listType) {
            case black:
            case white:
                row = paymentListRecordToRowConverter.convert(record.getListRecord());
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
