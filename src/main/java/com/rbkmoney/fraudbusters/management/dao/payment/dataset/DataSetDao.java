package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.domain.payment.DataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DataSetDao {

    Optional<Long> insert(DataSetModel dataSetModel);

    void remove(Long id);

    DataSetModel getById(Long id);

    List<DataSetModel> filter(LocalDateTime from, LocalDateTime to, FilterRequest filterRequest);

}
