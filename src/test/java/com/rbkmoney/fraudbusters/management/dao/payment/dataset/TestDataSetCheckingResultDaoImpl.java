package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {DataSetCheckingResultDaoImpl.class, DataSetDaoImpl.class,
        PaymentDaoImpl.class})
public class TestDataSetCheckingResultDaoImpl {

    public static final String TEST = "test";

    @Autowired
    private DataSetCheckingResultDaoImpl dataSetCheckingResultDaoById;
    @Autowired
    private DataSetDaoImpl dataSetDao;
    @Autowired
    private PaymentDaoImpl paymentDao;

    @Test
    void testDataSetCheckingResult() {
        PaymentModel paymentModel = createTestPaymentModel();
        Optional<Long> dataSetId = dataSetDao.insert(createDataSet(paymentModel));
        Long testDataSetId = dataSetId.get();
        paymentModel.setTestDataSetId(testDataSetId);
        paymentDao.insert(paymentModel);

        CheckedDataSetModel byId = dataSetCheckingResultDaoById.getById(testDataSetId);
        assertEquals(testDataSetId, byId.getTestDataSetId());
        assertEquals(Long.valueOf(1L), byId.getCheckedPaymentModels().get(0).getPaymentModel().getId());

        Optional<Long> insert = dataSetCheckingResultDaoById.insert(createCheckedDataSet(testDataSetId, 1L));

        assertTrue(insert.isPresent());

        byId = dataSetCheckingResultDaoById.getById(testDataSetId);

        assertEquals(TEST, byId.getTemplate());
        assertFalse(byId.getCheckedPaymentModels().isEmpty());
        assertEquals(Long.valueOf(1L), byId.getCheckedPaymentModels().get(0).getTestPaymentId());
        assertEquals(TEST, byId.getCheckedPaymentModels().get(0).getResultStatus());
        assertEquals(TEST, byId.getCheckedPaymentModels().get(0).getRuleChecked());
    }

    private CheckedDataSetModel createCheckedDataSet(Long testDataSetId, Long paymentId) {
        return CheckedDataSetModel.builder()
                .testDataSetId(testDataSetId)
                .checkedPaymentModels(List.of(CheckedPaymentModel.builder()
                        .testPaymentId(paymentId)
                        .resultStatus(TEST)
                        .ruleChecked(TEST)
                        .build()))
                .initiator(TEST)
                .template(TEST)
                .build();
    }

    private DataSetModel createDataSet(PaymentModel paymentModel) {
        return DataSetModel.builder()
                .name(TEST)
                .lastModificationInitiator(TEST)
                .template("sadasdsa")
                .paymentModelList(List.of(paymentModel))
                .build();
    }

    private PaymentModel createTestPaymentModel() {
        return PaymentModel.builder()
                .cardToken("cardToken")
                .amount(123L)
                .paymentId(TEST)
                .partyId(TEST)
                .shopId(TEST)
                .status(TEST)
                .lastModificationInitiator(TEST)
                .eventTime(LocalDateTime.now())
                .currency("RUB")
                .build();
    }
}
