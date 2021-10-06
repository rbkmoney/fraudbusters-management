package com.rbkmoney.fraudbusters.management.dao.payment.template;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FTemplateRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FTemplate.F_TEMPLATE;

@Component
public class PaymentTemplateDao extends AbstractDao implements TemplateDao {

    private final RowMapper<TemplateModel> listRecordRowMapper;

    public PaymentTemplateDao(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_TEMPLATE, TemplateModel.class);
    }

    @Override
    public void insert(TemplateModel templateModel) {
        templateModel.setLastUpdateDate(null);
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
        SelectConditionStep<FTemplateRecord> where = getDslContext()
                .selectFrom(F_TEMPLATE)
                .where(F_TEMPLATE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<String> getListNames(String idRegexp) {
        SelectConditionStep<Record1<String>> where = getDslContext()
                .select(F_TEMPLATE.ID)
                .from(F_TEMPLATE)
                .where(appendConditions(DSL.trueCondition(), Operator.AND, new ConditionParameterSource()
                        .addValue(F_TEMPLATE.ID, idRegexp, Comparator.LIKE)));
        return fetch(where, (resultSet, i) -> resultSet.getString(F_TEMPLATE.ID.getName()));
    }

    @Override
    public List<TemplateModel> filterModel(FilterRequest filterRequest) {
        FTemplateRecord filterTemplateRecord = new FTemplateRecord();
        filterTemplateRecord.setId(filterRequest.getLastId());
        SelectConditionStep<FTemplateRecord> where = getDslContext()
                .selectFrom(F_TEMPLATE)
                .where(StringUtils.hasLength(filterRequest.getSearchValue())
                        ? F_TEMPLATE.ID.like(filterRequest.getSearchValue())
                        : DSL.noCondition());
        SelectSeekStep1<FTemplateRecord, String> selectSeekStep =
                addSortCondition(F_TEMPLATE.ID, filterRequest.getSortOrder(), where);
        return fetch(addSeekIfNeed(filterRequest.getLastId(), filterRequest.getSize(), selectSeekStep),
                listRecordRowMapper);
    }

    @Override
    public Integer countFilterModel(String id) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(F_TEMPLATE)
                .where(StringUtils.hasLength(id) ? F_TEMPLATE.ID.like(id) : DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

}
