package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.utils.DataSourceBeanUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

class TestHistoricalDataSetCheckResultToTestCheckedDataSetModelConverter {

    HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter converter =
            new HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter(
                    new PaymentToTestPaymentModelConverter());

    @Test
    void convert() {
        CheckedDataSetModel model = converter.convert(new HistoricalDataSetCheckResult()
                .setHistoricalTransactionCheck(Set.of(new HistoricalTransactionCheck()
                        .setTransaction(DataSourceBeanUtils.createDamselPayment())
                        .setCheckResult(new CheckResult().setConcreteCheckResult(new ConcreteCheckResult()
                                .setResultStatus(ResultStatus.accept(new Accept())))))));

        assertEquals("accept", model.getCheckedPaymentModels().get(0).getResultStatus());
    }

}
