package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.DataSetCheckingResultDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.DataSetDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.PaymentDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.TestPaymentRecord;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.TestPayment.TEST_PAYMENT;
import static com.rbkmoney.fraudbusters.management.service.payment.DataSetModelUtils.TEST_INITIATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PostgresqlJooqITest
@ContextConfiguration(classes = {DataSetCheckingResultDaoImpl.class, PaymentsDataSetService.class,
        DataSetDaoImpl.class, PaymentDaoImpl.class})
public class PaymentsDataSetServiceTest {

    @Autowired
    PaymentsDataSetService paymentsDataSetService;

    @Autowired
    DSLContext dslContext;

    @Test
    public void filterDataSets() {
        LocalDateTime lastModificationDate = LocalDateTime.now();
        DataSetModel dataSetModel = DataSetModelUtils.initTestDataSetModel(lastModificationDate);
        dataSetModel.setLastModificationInitiator(TEST_INITIATOR);
        paymentsDataSetService.insertDataSet(dataSetModel);

        dataSetModel.setName(DataSetModelUtils.TEST + "2");
        dataSetModel.getPaymentModelList().get(0).setPaymentId(DataSetModelUtils.TEST + "2");
        paymentsDataSetService.insertDataSet(dataSetModel);

        DataSetModel dataSetModel3 = DataSetModelUtils.initTestDataSetModel(lastModificationDate.plusDays(1));
        dataSetModel3.setLastModificationInitiator(TEST_INITIATOR);
        paymentsDataSetService.insertDataSet(dataSetModel3);

        String from = lastModificationDate.minusDays(1L).format(DateTimeUtils.DATE_TIME_FORMATTER);
        String to = lastModificationDate.format(DateTimeUtils.DATE_TIME_FORMATTER);
        List<DataSetModel> dataSetModels = paymentsDataSetService.filterDataSets(
                from,
                to,
                FilterRequest.builder()
                        .size(10)
                        .build());

        assertEquals(2, dataSetModels.size());
    }

    @Test
    public void removeDataSets() {
        LocalDateTime lastModificationDate = LocalDateTime.now();
        DataSetModel dataSetModel = DataSetModelUtils.initTestDataSetModel(lastModificationDate);
        dataSetModel.setLastModificationInitiator(TEST_INITIATOR);
        Long idFirst = paymentsDataSetService.insertDataSet(dataSetModel);

        Result<TestPaymentRecord> dataSetModels = dslContext.fetch(TEST_PAYMENT);

        assertEquals(1, dataSetModels.size());

        paymentsDataSetService.removeDataSet(String.valueOf(idFirst), TEST_INITIATOR);

        dataSetModels = dslContext.fetch(TEST_PAYMENT);

        assertTrue(dataSetModels.isEmpty());
    }

    @Test
    void insertDataSet() {
        LocalDateTime lastModificationDate = LocalDateTime.now();
        Long id = paymentsDataSetService.insertDataSet(DataSetModelUtils.initTestDataSetModel(lastModificationDate));

        DataSetModel dataSet = paymentsDataSetService.getDataSet(String.valueOf(id));

        assertEquals(DataSetModelUtils.TEST, dataSet.getName());
        assertEquals(TEST_INITIATOR, dataSet.getLastModificationInitiator());

        dataSet.getPaymentModelList()
                .forEach(testPaymentModel -> {
                    assertEquals(DataSetModelUtils.PAYMENT_ID, testPaymentModel.getPaymentId());
                    assertEquals(DataSetModelUtils.PAYMENT_COUNTRY, testPaymentModel.getPaymentCountry());
                    assertEquals(DataSetModelUtils.PARTY_ID, testPaymentModel.getPartyId());
                    assertEquals(DataSetModelUtils.SHOP_ID, testPaymentModel.getShopId());
                });

        paymentsDataSetService.removeDataSet(String.valueOf(id), TEST_INITIATOR);
    }

}
