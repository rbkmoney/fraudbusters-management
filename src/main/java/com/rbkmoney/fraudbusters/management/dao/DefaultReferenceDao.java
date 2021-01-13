package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.DefaultReferenceModel;

public interface DefaultReferenceDao<T extends DefaultReferenceModel> {

    void insert(T referenceModel);

    void remove(String id);

    void remove(T referenceModel);

    T getById(String id);

}
