package com.rbkmoney.fraudbusters.management.dao.p2p.template;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFTemplateRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFTemplate.P2P_F_TEMPLATE;

@Component
public class P2pTemplateDao extends AbstractDao implements TemplateDao {

    private final RowMapper<TemplateModel> listRecordRowMapper;

    public P2pTemplateDao(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(P2P_F_TEMPLATE, TemplateModel.class);
    }

    @Override
    public void insert(TemplateModel templateModel) {
        templateModel.setLastUpdateDate(null);
        Query query = getDslContext()
                .insertInto(P2P_F_TEMPLATE)
                .set(getDslContext().newRecord(P2P_F_TEMPLATE, templateModel))
                .onConflict(P2P_F_TEMPLATE.ID)
                .doUpdate()
                .set(getDslContext().newRecord(P2P_F_TEMPLATE, templateModel));
        execute(query);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<P2pFTemplateRecord> where = getDslContext()
                .delete(P2P_F_TEMPLATE)
                .where(P2P_F_TEMPLATE.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(TemplateModel templateModel) {
        DeleteConditionStep<P2pFTemplateRecord> where = getDslContext()
                .delete(P2P_F_TEMPLATE)
                .where(P2P_F_TEMPLATE.ID.eq(templateModel.getId()));
        execute(where);
    }

    @Override
    public TemplateModel getById(String id) {
        SelectConditionStep<P2pFTemplateRecord> where = getDslContext()
                .selectFrom(P2P_F_TEMPLATE)
                .where(P2P_F_TEMPLATE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<TemplateModel> filterModel(FilterRequest filterRequest) {
        P2pFTemplateRecord p2pFTemplateRecord = new P2pFTemplateRecord();
        p2pFTemplateRecord.setId(filterRequest.getLastId());
        SelectConditionStep<P2pFTemplateRecord> where = getDslContext()
                .selectFrom(P2P_F_TEMPLATE)
                .where(StringUtils.hasLength(filterRequest.getSearchValue())
                        ? P2P_F_TEMPLATE.ID.like(filterRequest.getSearchValue())
                        : DSL.noCondition());
        SelectSeekStep1<P2pFTemplateRecord, String> selectSeekStep =
                addSortCondition(P2P_F_TEMPLATE.ID, filterRequest.getSortOrder(), where);
        return fetch(addSeekIfNeed(filterRequest.getLastId(), filterRequest.getSize(), selectSeekStep),
                listRecordRowMapper);
    }

    @Override
    public List<String> getListNames(String idRegexp) {
        SelectConditionStep<Record1<String>> where = getDslContext()
                .select(P2P_F_TEMPLATE.ID)
                .from(P2P_F_TEMPLATE)
                .where(appendConditions(DSL.trueCondition(), Operator.AND, new ConditionParameterSource()
                        .addValue(P2P_F_TEMPLATE.ID, idRegexp, Comparator.LIKE)));
        return fetch(where, (resultSet, i) -> resultSet.getString(P2P_F_TEMPLATE.ID.getName()));
    }

    @Override
    public Integer countFilterModel(String id) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(P2P_F_TEMPLATE)
                .where(StringUtils.hasLength(id) ? P2P_F_TEMPLATE.ID.like(id) : DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

}
