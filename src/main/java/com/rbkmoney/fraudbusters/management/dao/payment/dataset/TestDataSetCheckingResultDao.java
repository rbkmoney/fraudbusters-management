package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;

import java.util.Optional;

public interface TestDataSetCheckingResultDao {

    Optional<Long> insert(TestCheckedDataSetModel dataSetModel);

    TestCheckedDataSetModel getById(Long id);

}
