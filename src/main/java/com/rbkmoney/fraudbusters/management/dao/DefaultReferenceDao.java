package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.DefaultReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;

import java.util.List;

public interface DefaultReferenceDao<T extends DefaultReferenceModel> {

    void insert(T referenceModel);

    void remove(String id);

    T getById(String id);

    List<T> filterReferences(FilterRequest filterRequest);

    Integer countFilterModel(String searchValue);
}
