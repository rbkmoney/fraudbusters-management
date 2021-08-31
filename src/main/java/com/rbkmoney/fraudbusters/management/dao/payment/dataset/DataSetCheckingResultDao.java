package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.domain.payment.CheckedDataSetModel;

import java.util.Optional;

public interface DataSetCheckingResultDao {

    Optional<Long> insert(CheckedDataSetModel dataSetModel);

    CheckedDataSetModel getById(Long id);

}
