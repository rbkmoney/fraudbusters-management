package com.rbkmoney.fraudbusters.management.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCheckedPaymentModel {

    private TestPaymentModel testPaymentModel;

    private Long testDataSetCheckingResultId;
    private Long testPaymentId;
    private String checkedTemplate;
    private String resultStatus;
    private String ruleChecked;
    private List<String> notificationRule;

}
