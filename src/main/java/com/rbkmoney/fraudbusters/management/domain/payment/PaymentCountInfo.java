package com.rbkmoney.fraudbusters.management.domain.payment;

import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import lombok.Data;

@Data
public class PaymentCountInfo {

    private CountInfo countInfo;
    private PaymentListRecord listRecord;

}
