package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;

import java.util.List;

public interface GroupReferenceDao<T extends GroupReferenceModel> {

    void insert(T referenceModel);

    void remove(T referenceModel);

    List<T> getByGroupId(String id);

}
