package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.dataset.DataSetCheckingResultDao;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.DataSetDao;
import com.rbkmoney.fraudbusters.management.dao.payment.dataset.PaymentDao;
import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
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

    private final DataSetDao dataSetDao;
    private final DataSetCheckingResultDao dataSetCheckingResultDao;
    private final PaymentDao paymentDao;

    public List<DataSetModel> filterDataSets(String from, String to, FilterRequest filterRequest) {
        var fromDate = DateTimeUtils.parse(from);
        var toDate = DateTimeUtils.parse(to);
        filterRequest.setSearchValue(FilterRequestUtils.prepareSearchValue(filterRequest.getSearchValue()));
        return dataSetDao.filter(fromDate, toDate, filterRequest);
    }

    @Transactional
    public void removeDataSet(String id, String initiator) {
        var idDataSet = Long.valueOf(id);
        paymentDao.removeByDataSetId(idDataSet);
        dataSetDao.remove(idDataSet);
    }

    public DataSetModel getDataSet(String id) {
        var idDataSet = Long.valueOf(id);
        var testDataSetModel = dataSetDao.getById(idDataSet);
        List<PaymentModel> byDataSetId = paymentDao.getByDataSetId(idDataSet);
        testDataSetModel.setPaymentModelList(byDataSetId);
        return testDataSetModel;
    }

    public CheckedDataSetModel getCheckedDataSet(String id) {
        var idDataSet = Long.valueOf(id);
        return dataSetCheckingResultDao.getById(idDataSet);
    }

    @Transactional
    public Long insertDataSet(DataSetModel dataSetModel) {
        Optional<Long> id = dataSetDao.insert(dataSetModel);
        if (id.isPresent() && !CollectionUtils.isEmpty(dataSetModel.getPaymentModelList())) {
            List<PaymentModel> paymentModelList = dataSetModel.getPaymentModelList();
            paymentDao.insertBatch(paymentModelList.stream()
                    .map(testPaymentModel -> updateModel(dataSetModel.getLastModificationInitiator(), id.get(),
                            testPaymentModel))
                    .collect(Collectors.toList()));
        }
        return id.orElse(null);
    }

    public Long insertCheckedDataSet(CheckedDataSetModel dataSetModel, String initiator) {
        dataSetModel.setInitiator(initiator);
        Optional<Long> id = dataSetCheckingResultDao.insert(dataSetModel);
        return id.orElse(null);
    }

    private PaymentModel updateModel(String initiator, Long id,
                                     PaymentModel paymentModel) {
        paymentModel.setTestDataSetId(id);
        paymentModel.setLastModificationInitiator(initiator);
        paymentModel.setLastModificationDate(null);
        return paymentModel;
    }

}
