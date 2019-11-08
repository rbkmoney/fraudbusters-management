package com.rbkmoney.fraudbusters.management.dao.payment.template;

import com.rbkmoney.fraudbusters.management.domain.TemplateModel;

import java.util.List;
import java.util.Set;

public interface TemplateDao {

    void insert(TemplateModel listRecord);

    void remove(String id);

    void remove(TemplateModel listRecord);

    TemplateModel getById(String id);

    List<TemplateModel> getList(int limit);
}
