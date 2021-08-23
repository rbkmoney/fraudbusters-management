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
public class TestCheckedDataSetModel {

    private Long id;
    private Long testDataSetId;
    private String createdAt;
    private String initiator;
    private String template;

    private String partyId;
    private String shopId;
    private String checkingTimestamp;

    private List<TestCheckedPaymentModel> testCheckedPaymentModels;

}