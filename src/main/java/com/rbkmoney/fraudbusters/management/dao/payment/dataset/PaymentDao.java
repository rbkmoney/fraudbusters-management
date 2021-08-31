package com.rbkmoney.fraudbusters.management.dao.payment.dataset;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentModel;

import java.util.List;

public interface PaymentDao {

    void insert(PaymentModel dataSetModel);

    void insertBatch(List<PaymentModel> dataSetModels);

    void remove(Long id);

    List<PaymentModel> getByDataSetId(Long id);

    void removeByDataSetId(Long id);

}
