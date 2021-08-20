package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {TestDataSetCheckingResultDaoImpl.class, TestDataSetDaoImpl.class,
        TestPaymentDaoImpl.class})
public class TestDataSetCheckingResultDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String TEST = "test";

    @Autowired
    private TestDataSetCheckingResultDaoImpl testDataSetCheckingResultDao;
    @Autowired
    private TestDataSetDaoImpl testDataSetDao;
    @Autowired
    private TestPaymentDaoImpl testPaymentDao;

    @Test
    public void testDataSetCheckingResult() {
        TestPaymentModel testPaymentModel = createTestPaymentModel();
        Optional<Long> dataSetId = testDataSetDao.insert(createDataSet(testPaymentModel));
        Long testDataSetId = dataSetId.get();
        testPaymentModel.setTestDataSetId(testDataSetId);

        testPaymentDao.insert(testPaymentModel);
        Optional<Long> insert = testDataSetCheckingResultDao.insert(createCheckedDataSet(testDataSetId));

        assertTrue(insert.isPresent());

        TestCheckedDataSetModel byId = testDataSetCheckingResultDao.getById(testDataSetId);
        System.out.println(byId);
        assertEquals(TEST, byId.getTemplate());
        assertFalse(byId.getTestCheckedPaymentModels().isEmpty());

        assertEquals(TEST, byId.getTestCheckedPaymentModels().get(0).getTestPaymentId());
    }

    private TestCheckedDataSetModel createCheckedDataSet(Long testDataSetId) {
        return TestCheckedDataSetModel.builder()
                .testDataSetId(testDataSetId)
                .testCheckedPaymentModels(List.of(TestCheckedPaymentModel.builder()
                        .resultStatus(TEST)
                        .ruleChecked(TEST)
                        .testPaymentId(TEST)
                        .build()))
                .initiator(TEST)
                .template(TEST)
                .build();
    }

    private TestDataSetModel createDataSet(TestPaymentModel testPaymentModel) {
        return TestDataSetModel.builder()
                .name(TEST)
                .lastModificationInitiator(TEST)
                .template("sadasdsa")
                .testPaymentModelList(List.of(testPaymentModel))
                .build();
    }

    private TestPaymentModel createTestPaymentModel() {
        return TestPaymentModel.builder()
                .cardToken("cardToken")
                .amount(123L)
                .paymentId(TEST)
                .partyId(TEST)
                .shopId(TEST)
                .status(TEST)
                .lastModificationInitiator(TEST)
                .eventTime(Instant.now().toString())
                .currency("RUB")
                .build();
    }

}
