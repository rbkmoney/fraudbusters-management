package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCountInfoListRequestGenerator {

    private final CountInfoUtils countInfoGenerator;

    public PaymentCountInfo initDestination(String rowInfo, PaymentListRecord listRecord) {
        PaymentCountInfo paymentCountInfo = new PaymentCountInfo();
        paymentCountInfo.setListRecord(listRecord);
        if (!StringUtil.isNullOrEmpty(rowInfo)) {
            CountInfo countInfoValue = countInfoGenerator.initRowCountInfo(rowInfo);
            paymentCountInfo.setCountInfo(countInfoValue);
        }
        return paymentCountInfo;
    }

}
