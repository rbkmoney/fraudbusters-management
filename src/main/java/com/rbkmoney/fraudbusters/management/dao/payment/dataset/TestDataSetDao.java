package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;

import java.util.List;
import java.util.Optional;

public interface TestDataSetDao {

    Optional<Long> insert(TestDataSetModel dataSetModel);

    void remove(Long id);

    TestDataSetModel getById(Long id);

    List<TestDataSetModel> filter(FilterRequest filterRequest);

}
