package com.rbkmoney.fraudbusters.management.domain;

import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.damsel.domain.PaymentRoute;
import com.rbkmoney.damsel.fraudbusters.FraudInfo;
import com.rbkmoney.damsel.fraudbusters.PayerType;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import lombok.Data;

@Data
public class FraudPaymentRecord {

    public String id;
    public String eventTime;

    public String partyId;
    public String shopId;

    public long amount;
    public String currency;

    public PayerType payerType;

    public Payer payer;
    public PaymentStatus status;
    public String rrn;
    public PaymentRoute route;
    public FraudInfo fraud_info;

}
