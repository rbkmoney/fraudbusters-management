package com.rbkmoney.fraudbusters.management.dao.template;

import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.exception.DaoException;

import java.util.List;

public interface TemplateDao {

    void insert(TemplateModel listRecord) throws DaoException;

    void remove(String id) throws DaoException;

    void remove(TemplateModel listRecord) throws DaoException;

    TemplateModel getById(String id) throws DaoException;

    List<TemplateModel> getList(int limit) throws DaoException;
}
