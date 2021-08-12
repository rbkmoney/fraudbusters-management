package com.rbkmoney.fraudbusters.management.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestPaymentModel {

    private Long id;
    private Long testDataSetId;

    private String lastModificationDate;
    private String lastModificationInitiator;

    private String paymentId;
    private String eventTime;

    private Long amount;
    private String currency;

    private String cardToken;
    private String status;

    private String payerType;

    private String paymentSystem;
    private String paymentCountry;
    private String paymentTool;

    private Boolean mobile;
    private Boolean recurrent;

    private String partyId;
    private String shopId;

    private String ip;
    private String fingerprint;
    private String email;

    private String errorCode;
    private String errorReason;

    private String providerId;
    private String terminalId;
    private String country;
}
