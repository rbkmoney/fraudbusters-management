package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.TestDataSetCheckingResultDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.TestDataSetDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.TestPaymentDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.service.payment.DataSetModelUtils.TEST_INITIATOR;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {TestDataSetCheckingResultDaoImpl.class, PaymentsDataSetService.class,
        TestDataSetDaoImpl.class, TestPaymentDaoImpl.class})
public class PaymentsDataSetServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    PaymentsDataSetService paymentsDataSetService;

    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void filterDataSets() {
        LocalDateTime lastModificationDate = LocalDateTime.now();
        TestDataSetModel testDataSetModel = DataSetModelUtils.initTestDataSetModel(lastModificationDate);
        Long idFirst = paymentsDataSetService
                .insertDataSet(testDataSetModel,
                        TEST_INITIATOR);

        testDataSetModel.setName(DataSetModelUtils.TEST + "2");
        testDataSetModel.getTestPaymentModelList().get(0).setPaymentId(DataSetModelUtils.TEST + "2");
        Long idSecond = paymentsDataSetService
                .insertDataSet(testDataSetModel, TEST_INITIATOR);

        String from = lastModificationDate.minusDays(1L).format(DateTimeUtils.DATE_TIME_FORMATTER);
        String to = lastModificationDate.format(DateTimeUtils.DATE_TIME_FORMATTER);
        List<TestDataSetModel> testDataSetModels = paymentsDataSetService.filterDataSets(
                from,
                to,
                FilterRequest.builder()
                        .size(10)
                        .build());

        assertEquals(2, testDataSetModels.size());

        paymentsDataSetService.removeDataSet(String.valueOf(idFirst), TEST_INITIATOR);
        testDataSetModels = paymentsDataSetService.filterDataSets(
                from,
                to,
                FilterRequest.builder()
                        .size(10)
                        .build());

        assertEquals(1, testDataSetModels.size());

        paymentsDataSetService.removeDataSet(String.valueOf(idSecond), TEST_INITIATOR);
        testDataSetModels = paymentsDataSetService.filterDataSets(
                from,
                to,
                FilterRequest.builder()
                        .size(10)
                        .build());

        assertEquals(0, testDataSetModels.size());
    }

    @Test
    public void insertDataSet() {
        LocalDateTime lastModificationDate = LocalDateTime.now();
        Long id = paymentsDataSetService.insertDataSet(DataSetModelUtils.initTestDataSetModel(lastModificationDate),
                TEST_INITIATOR);

        TestDataSetModel dataSet = paymentsDataSetService.getDataSet(String.valueOf(id));

        assertEquals(DataSetModelUtils.TEST, dataSet.getName());
        assertEquals(TEST_INITIATOR, dataSet.getLastModificationInitiator());

        dataSet.getTestPaymentModelList()
                .forEach(testPaymentModel -> {
                    assertEquals(DataSetModelUtils.PAYMENT_ID, testPaymentModel.getPaymentId());
                    assertEquals(DataSetModelUtils.PAYMENT_COUNTRY, testPaymentModel.getPaymentCountry());
                    assertEquals(DataSetModelUtils.PARTY_ID, testPaymentModel.getPartyId());
                    assertEquals(DataSetModelUtils.SHOP_ID, testPaymentModel.getShopId());
                });

        paymentsDataSetService.removeDataSet(String.valueOf(id), TEST_INITIATOR);

    }

}
