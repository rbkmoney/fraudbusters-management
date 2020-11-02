package com.rbkmoney.fraudbusters.management.domain.payment.request;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListRowsInsertRequest {

    private ListType listType;
    private List<PaymentCountInfo> records;
}
