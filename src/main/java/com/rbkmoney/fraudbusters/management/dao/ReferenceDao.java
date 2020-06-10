package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;

import java.util.List;

public interface ReferenceDao<T extends ReferenceModel> {

    void insert(T referenceModel);

    void remove(String id);

    void remove(T referenceModel);

    T getById(String id);

    List<T> getList(Integer limit);

    List<T> getListByTemplateId(String templateId, Integer limit);

    T getGlobalReference();

}
