package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.domain.payment.TestPaymentModel;

import java.util.List;

public interface TestPaymentDao {

    void insert(TestPaymentModel dataSetModel);

    void insertBatch(List<TestPaymentModel> dataSetModels);

    void remove(Long id);

    List<TestPaymentModel> getByDataSetId(Long id);

    void removeByDataSetId(Long id);

}
