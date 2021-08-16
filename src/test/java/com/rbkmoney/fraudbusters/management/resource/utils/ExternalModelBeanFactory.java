package com.rbkmoney.fraudbusters.management.resource.utils;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalModelBeanFactory {

    public static PaymentTool createPaymentToolBankCard() {
        BankCard bankCard = new BankCard()
                .setBankName("test")
                .setBin("1234")
                .setPaymentSystem(new PaymentSystemRef()
                        .setId("visa"));
        PaymentTool paymentTool = new PaymentTool();
        paymentTool.setBankCard(bankCard);
        return paymentTool;
    }

    public static Payment createPayment(PaymentTool paymentTool, ReferenceInfo referenceInfo) {
        return new Payment()
                .setId("test")
                .setMobile(false)
                .setEventTime("2021-07-29T13:16:18.348795")
                .setPaymentTool(paymentTool)
                .setClientInfo(createClientInfo())
                .setCost(createCash())
                .setProviderInfo(createProviderInfo())
                .setReferenceInfo(referenceInfo)
                .setStatus(PaymentStatus.captured);
    }

    private static Cash createCash() {
        return new Cash()
                .setAmount(123L)
                .setCurrency(new CurrencyRef()
                        .setSymbolicCode("RUB"));
    }

    public static Refund createRefund(PaymentTool paymentTool, ReferenceInfo referenceInfo) {
        return new Refund()
                .setId("test")
                .setEventTime("2021-07-29T13:16:18.348795")
                .setPaymentTool(paymentTool)
                .setClientInfo(createClientInfo())
                .setCost(createCash())
                .setProviderInfo(createProviderInfo())
                .setReferenceInfo(referenceInfo)
                .setStatus(RefundStatus.succeeded);
    }

    public static HistoricalTransactionCheck createCheckTransactions(PaymentTool paymentTool,
                                                                     ReferenceInfo referenceInfo) {
        return new HistoricalTransactionCheck()
                .setTransaction(createPayment(paymentTool, referenceInfo))
                .setCheckResult(new CheckResult()
                        .setConcreteCheckResult(new ConcreteCheckResult()
                                .setResultStatus(ResultStatus.accept(new Accept()))));
    }

    public static FraudPaymentInfo createFraudPaymentInfos(PaymentTool paymentTool,
                                                           ReferenceInfo referenceInfo) {
        return new FraudPaymentInfo()
                .setPayment(createPayment(paymentTool, referenceInfo))
                .setComment("test_comment")
                .setType("type_test");
    }

    public static Chargeback createChargebacks(PaymentTool paymentTool,
                                               ReferenceInfo referenceInfo) {
        return new Chargeback()
                .setId("test")
                .setEventTime("2021-07-29T13:16:18.348795")
                .setPaymentTool(paymentTool)
                .setClientInfo(createClientInfo())
                .setCost(createCash())
                .setProviderInfo(createProviderInfo())
                .setReferenceInfo(referenceInfo)
                .setCategory(ChargebackCategory.authorisation)
                .setChargebackCode("123")
                .setStatus(ChargebackStatus.accepted);
    }

    public static ReferenceInfo createReferenceInfo() {
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(new MerchantInfo()
                .setPartyId("party")
                .setShopId("shop"));
        return referenceInfo;
    }

    private static ProviderInfo createProviderInfo() {
        return new ProviderInfo()
                .setProviderId("test")
                .setCountry("RUS")
                .setTerminalId("1234");
    }

    private static ClientInfo createClientInfo() {
        return new ClientInfo()
                .setEmail("email")
                .setFingerprint("finger")
                .setIp("123.123.123.123");
    }


    public static HistoricalData createHistoricalData() {
        HistoricalData historicalData = new HistoricalData();
        PaymentTool paymentTool = createPaymentToolBankCard();
        ReferenceInfo referenceInfo = createReferenceInfo();
        historicalData.setPayments(List.of(createPayment(paymentTool, referenceInfo)));
        return historicalData;
    }

    public static HistoricalData createHistoricalDataRefunds() {
        HistoricalData historicalData = new HistoricalData();
        PaymentTool paymentTool = createPaymentToolBankCard();
        ReferenceInfo referenceInfo = createReferenceInfo();
        historicalData.setRefunds(List.of(createRefund(paymentTool, referenceInfo)));
        return historicalData;
    }

    public static HistoricalData createHistoricalDataInspectResults() {
        HistoricalData historicalData = new HistoricalData();
        PaymentTool paymentTool = createPaymentToolBankCard();
        ReferenceInfo referenceInfo = createReferenceInfo();
        historicalData
                .setFraudResults(List.of(createCheckTransactions(paymentTool, referenceInfo)));
        return historicalData;
    }

    public static HistoricalData createHistoricalDataFraudPaymentInfos() {
        HistoricalData historicalData = new HistoricalData();
        PaymentTool paymentTool = createPaymentToolBankCard();
        ReferenceInfo referenceInfo = createReferenceInfo();
        historicalData.setFraudPayments(
                List.of(createFraudPaymentInfos(paymentTool, referenceInfo)));
        return historicalData;
    }

    public static HistoricalData createHistoricalDataChargebacks() {
        HistoricalData historicalData = new HistoricalData();
        PaymentTool paymentTool = createPaymentToolBankCard();
        ReferenceInfo referenceInfo = createReferenceInfo();
        historicalData.setChargebacks(
                List.of(createChargebacks(paymentTool, referenceInfo)));
        return historicalData;
    }
}
