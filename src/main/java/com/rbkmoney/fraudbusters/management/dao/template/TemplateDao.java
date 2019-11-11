package com.rbkmoney.fraudbusters.management.dao.template;

import com.rbkmoney.fraudbusters.management.domain.TemplateModel;

import java.util.List;

public interface TemplateDao {

    void insert(TemplateModel listRecord);

    void remove(String id);

    void remove(TemplateModel listRecord);

    TemplateModel getById(String id);

    List<TemplateModel> getList(int limit);
}
