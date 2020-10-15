package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import org.jooq.SortOrder;

import java.util.List;

public interface TemplateDao {

    void insert(TemplateModel listRecord);

    void remove(String id);

    void remove(TemplateModel listRecord);

    TemplateModel getById(String id);

    List<TemplateModel> getList(Integer limit);

    List<String> getListNames(String idRegexp);

    List<TemplateModel> filterModel(String id, String lastId, Integer size, SortOrder sortOrder);

    Integer countFilterModel(String id);
}
