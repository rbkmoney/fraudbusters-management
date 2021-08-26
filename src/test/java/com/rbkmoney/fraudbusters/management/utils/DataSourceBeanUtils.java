package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.swag.fraudbusters.management.model.ApplyRuleOnHistoricalDataSetRequest;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Set;

public class DataSourceBeanUtils {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";

    public static HistoricalDataSetCheckResult createHistoricalResponse() {
        return new HistoricalDataSetCheckResult()
                .setHistoricalTransactionCheck(Set.of(new HistoricalTransactionCheck()
                        .setCheckResult(new CheckResult()
                                .setConcreteCheckResult(new ConcreteCheckResult()
                                        .setResultStatus(ResultStatus.accept(new Accept()))
                                        .setRuleChecked("test"))
                                .setCheckedTemplate("test"))
                        .setTransaction(new com.rbkmoney.damsel.fraudbusters.Payment()
                                .setId("1")
                                .setCost(new Cash()
                                        .setCurrency(new CurrencyRef()
                                                .setSymbolicCode("RUB"))
                                        .setAmount(100L))
                                .setPayerType(PayerType.payment_resource)
                                .setStatus(PaymentStatus.captured)
                                .setPaymentTool(PaymentTool.bank_card(new BankCard())))));
    }

    public static com.rbkmoney.swag.fraudbusters.management.model.Payment createPayment() {
        return new com.rbkmoney.swag.fraudbusters.management.model.Payment()
                .amount(100L)
                .currency("RUB")
                .id("1")
                .mobile(false)
                .recurrent(false)
                .status(com.rbkmoney.swag.fraudbusters.management.model.Payment.StatusEnum.CAPTURED)
                .merchantInfo(new com.rbkmoney.swag.fraudbusters.management.model.MerchantInfo()
                        .partyId(PARTY_ID)
                        .shopId(SHOP_ID));
    }

    public static ApplyRuleOnHistoricalDataSetRequest createApplyRequst(
            com.rbkmoney.swag.fraudbusters.management.model.Payment payment) {
        return new ApplyRuleOnHistoricalDataSetRequest()
                .dataSetId(1L)
                .reference(new PaymentReference()
                        .partyId(PARTY_ID)
                        .shopId(SHOP_ID))
                .template("test")
                .records(List.of(payment));
    }

    public static LinkedMultiValueMap<String, String> createParams() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", "test");
        params.add("size", "100");
        params.add("from", "2021-07-27 00:00:00");
        params.add("to", "2021-07-27 13:28:54");
        return params;
    }
}
