package com.rbkmoney.fraudbusters.management.dao.reference;

import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.exception.DaoException;

import java.util.List;

public interface ReferenceDao {

    void insert(ReferenceModel referenceModel) throws DaoException;

    void remove(String id) throws DaoException;

    void remove(ReferenceModel referenceModel) throws DaoException;

    ReferenceModel getById(String id) throws DaoException;

    List<ReferenceModel> getList(int limit) throws DaoException;

    List<ReferenceModel> getListByTemplateId(String templateId, int limit) throws DaoException;
}
