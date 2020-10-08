package com.rbkmoney.fraudbusters.management.dao.p2p.template;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
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

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<TemplateModel> listRecordRowMapper;

    public P2pTemplateDao(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(P2P_F_TEMPLATE, TemplateModel.class);
    }

    @Override
    public void insert(TemplateModel templateModel) {
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
        SelectConditionStep<Record2<String, String>> where = getDslContext()
                .select(P2P_F_TEMPLATE.ID, P2P_F_TEMPLATE.TEMPLATE)
                .from(P2P_F_TEMPLATE)
                .where(P2P_F_TEMPLATE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<TemplateModel> getList(Integer limit) {
        SelectLimitPercentStep<Record2<String, String>> query = getDslContext()
                .select(P2P_F_TEMPLATE.ID, P2P_F_TEMPLATE.TEMPLATE)
                .from(P2P_F_TEMPLATE)
                .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public List<TemplateModel> filterModel(String id, String lastId, Integer size, SortOrder sortOrder) {
        P2pFTemplateRecord p2pFTemplateRecord = new P2pFTemplateRecord();
        p2pFTemplateRecord.setId(lastId);
        SelectConditionStep<P2pFTemplateRecord> where = getDslContext()
                .selectFrom(P2P_F_TEMPLATE)
                .where(!StringUtils.isEmpty(id) ? P2P_F_TEMPLATE.ID.like(id) : DSL.noCondition());
        SelectSeekStep1<P2pFTemplateRecord, String> selectSeekStep = addSortCondition(P2P_F_TEMPLATE.ID, sortOrder, where);
        return fetch(addSeekIfNeed(lastId, size, selectSeekStep), listRecordRowMapper);
    }

    @Override
    public Integer countFilterModel(String id) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(P2P_F_TEMPLATE)
                .where(!StringUtils.isEmpty(id) ? P2P_F_TEMPLATE.ID.like(id) : DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

}
