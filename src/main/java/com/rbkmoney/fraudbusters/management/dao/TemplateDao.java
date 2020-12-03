package com.rbkmoney.fraudbusters.management.dao;

import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import org.jooq.SortOrder;

import java.util.List;

public interface TemplateDao {

    void insert(TemplateModel listRecord);

    void remove(String id);

    void remove(TemplateModel listRecord);

    TemplateModel getById(String id);

    List<TemplateModel> getList(Integer limit);

    List<String> getListNames(String idRegexp);

    List<TemplateModel> filterModel(FilterRequest filterRequest);

    Integer countFilterModel(String id);
}
