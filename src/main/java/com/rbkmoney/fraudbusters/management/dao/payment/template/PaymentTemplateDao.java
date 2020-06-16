package com.rbkmoney.fraudbusters.management.dao.payment.template;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FTemplateRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FTemplate.F_TEMPLATE;

@Component
public class PaymentTemplateDao extends AbstractDao implements TemplateDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<TemplateModel> listRecordRowMapper;

    public PaymentTemplateDao(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_TEMPLATE, TemplateModel.class);
    }

    @Override
    public void insert(TemplateModel templateModel) {
        Query query = getDslContext()
                .insertInto(F_TEMPLATE)
                .set(getDslContext().newRecord(F_TEMPLATE, templateModel))
                .onConflict(F_TEMPLATE.ID)
                .doUpdate()
                .set(getDslContext().newRecord(F_TEMPLATE, templateModel));
        execute(query);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<FTemplateRecord> where = getDslContext()
                .delete(F_TEMPLATE)
                .where(F_TEMPLATE.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(TemplateModel templateModel) {
        DeleteConditionStep<FTemplateRecord> where = getDslContext()
                .delete(F_TEMPLATE)
                .where(F_TEMPLATE.ID.eq(templateModel.getId()));
        execute(where);
    }

    @Override
    public TemplateModel getById(String id) {
        SelectConditionStep<Record2<String, String>> where = getDslContext()
                .select(F_TEMPLATE.ID, F_TEMPLATE.TEMPLATE)
                .from(F_TEMPLATE)
                .where(F_TEMPLATE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<TemplateModel> getList(Integer limit) {
        SelectLimitPercentStep<Record2<String, String>> query = getDslContext()
                .select(F_TEMPLATE.ID, F_TEMPLATE.TEMPLATE)
                .from(F_TEMPLATE)
                .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }


}
