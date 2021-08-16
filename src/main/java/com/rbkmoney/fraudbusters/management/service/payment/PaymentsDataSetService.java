package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.dataset.TestDataSetDao;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.TestPaymentDao;
import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.utils.FilterRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentsDataSetService {

    private final TestDataSetDao testDataSetDao;
    private final TestPaymentDao testPaymentDao;

    public List<TestDataSetModel> filterDataSets(FilterRequest filterRequest) {
        filterRequest.setSearchValue(FilterRequestUtils.prepareSearchValue(filterRequest.getSearchValue()));
        return testDataSetDao.filter(filterRequest);
    }

    @Transactional
    public void removeDataSet(String id, String initiator) {
        var idDataSet = Long.valueOf(id);
        testPaymentDao.removeByDataSetId(idDataSet);
        testDataSetDao.remove(idDataSet);
    }

    public TestDataSetModel getDataSet(String id) {
        var idDataSet = Long.valueOf(id);
        var testDataSetModel = testDataSetDao.getById(idDataSet);
        List<TestPaymentModel> byDataSetId = testPaymentDao.getByDataSetId(idDataSet);
        testDataSetModel.setTestPaymentModelList(byDataSetId);
        return testDataSetModel;
    }

    @Transactional
    public Long insertDataSet(TestDataSetModel dataSetModel, String initiator) {
        dataSetModel.setLastModificationInitiator(initiator);
        Optional<Long> id = testDataSetDao.insert(dataSetModel);
        if (id.isPresent() && !CollectionUtils.isEmpty(dataSetModel.getTestPaymentModelList())) {
            List<TestPaymentModel> testPaymentModelList = dataSetModel.getTestPaymentModelList();
            testPaymentDao.insertBatch(testPaymentModelList.stream()
                    .map(testPaymentModel -> updateModel(initiator, id, testPaymentModel))
                    .collect(Collectors.toList()));
        }
        return id.orElse(null);
    }

    private TestPaymentModel updateModel(String initiator, Optional<Long> id,
                                         TestPaymentModel testPaymentModel) {
        testPaymentModel.setTestDataSetId(id.get());
        testPaymentModel.setLastModificationInitiator(initiator);
        testPaymentModel.setLastModificationDate(null);
        return testPaymentModel;
    }

}
