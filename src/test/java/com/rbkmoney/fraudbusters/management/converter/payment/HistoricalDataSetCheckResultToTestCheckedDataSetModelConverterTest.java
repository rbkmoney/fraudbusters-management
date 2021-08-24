package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

class HistoricalDataSetCheckResultToTestCheckedDataSetModelConverterTest {

    HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter converter =
            new HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter(
                    new PaymentToTestPaymentModelConverter());


    @Test
    void convert() {
        TestCheckedDataSetModel model = converter.convert(new HistoricalDataSetCheckResult()
                .setHistoricalTransactionCheck(Set.of(new HistoricalTransactionCheck()
                        .setTransaction(new Payment()
                                .setId("1")
                                .setPaymentTool(PaymentTool.bank_card(new BankCard()))
                                .setCost(new Cash()
                                        .setAmount(12L)
                                        .setCurrency(new CurrencyRef()
                                                .setSymbolicCode("RUB")))
                                .setStatus(PaymentStatus.captured)
                                .setPayerType(PayerType.payment_resource)
                        )
                        .setCheckResult(new CheckResult().setConcreteCheckResult(new ConcreteCheckResult()
                                .setResultStatus(ResultStatus.accept(new Accept())))))));

        assertEquals("accept", model.getTestCheckedPaymentModels().get(0).getResultStatus());
    }
}
