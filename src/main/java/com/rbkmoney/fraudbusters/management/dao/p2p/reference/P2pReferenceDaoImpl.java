package com.rbkmoney.fraudbusters.management.dao.p2p.reference;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFReferenceRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFReference.P2P_F_REFERENCE;
import static org.jooq.Comparator.EQUALS;
import static org.jooq.Comparator.LIKE;

@Component
public class P2pReferenceDaoImpl extends AbstractDao implements P2pReferenceDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<P2pReferenceModel> listRecordRowMapper;

    public P2pReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(P2P_F_REFERENCE, P2pReferenceModel.class);
    }

    @Override
    public void insert(P2pReferenceModel referenceModel) {
        referenceModel.setLastUpdateDate(null);
        Query query = getDslContext().insertInto(P2P_F_REFERENCE)
                .set(getDslContext().newRecord(P2P_F_REFERENCE, referenceModel))
                .onConflict(P2P_F_REFERENCE.IDENTITY_ID, P2P_F_REFERENCE.IS_GLOBAL)
                .doUpdate()
                .set(getDslContext().newRecord(P2P_F_REFERENCE, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<P2pFReferenceRecord> where = getDslContext()
                .delete(P2P_F_REFERENCE)
                .where(P2P_F_REFERENCE.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(P2pReferenceModel referenceModel) {
        Condition condition = DSL.trueCondition();
        DeleteConditionStep<P2pFReferenceRecord> where = getDslContext()
                .delete(P2P_F_REFERENCE)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(P2P_F_REFERENCE.IDENTITY_ID, referenceModel.getIdentityId(), EQUALS)));
        execute(where);
    }

    @Override
    public P2pReferenceModel getById(String id) {
        SelectConditionStep<P2pFReferenceRecord> where = getDslContext()
                .selectFrom(P2P_F_REFERENCE)
                .where(P2P_F_REFERENCE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<P2pReferenceModel> getListByTFilters(String identityId, Boolean isGlobal, Integer limit) {
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep<P2pFReferenceRecord> referenceRecords = getDslContext()
                .selectFrom(P2P_F_REFERENCE)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(P2P_F_REFERENCE.IDENTITY_ID, identityId, EQUALS)
                                .addValue(P2P_F_REFERENCE.IS_GLOBAL, isGlobal, EQUALS)))
                .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(referenceRecords, listRecordRowMapper);
    }

    @Override
    public P2pReferenceModel getGlobalReference() {
        return fetchOne(getDslContext()
                        .selectFrom(P2P_F_REFERENCE)
                        .where(P2P_F_REFERENCE.IS_GLOBAL.eq(true)),
                listRecordRowMapper);
    }

    @Override
    public List<P2pReferenceModel> filterReferences(FilterRequest filterRequest, boolean isGlobal) {
        SelectWhereStep<P2pFReferenceRecord> from = getDslContext()
                .selectFrom(P2P_F_REFERENCE);
        Field<String> field = StringUtils.isEmpty(filterRequest.getSortBy()) ? P2P_F_REFERENCE.TEMPLATE_ID :
                P2P_F_REFERENCE.field(filterRequest.getSortBy(), String.class);
        SelectConditionStep<P2pFReferenceRecord> whereQuery = StringUtils.isEmpty(filterRequest.getSearchValue())
                ? from.where(DSL.trueCondition())
                : from.where(P2P_F_REFERENCE.TEMPLATE_ID.like(filterRequest.getSearchValue())
                        .or(P2P_F_REFERENCE.IDENTITY_ID.like(filterRequest.getSearchValue())));
        SelectSeekStep2<P2pFReferenceRecord, String, String> filterReferenceRecords = addSortCondition(
                P2P_F_REFERENCE.ID, field, filterRequest.getSortOrder(), whereQuery);
        return fetch(
                addSeekIfNeed(filterRequest.getLastId(), filterRequest.getSortFieldValue(), filterRequest.getSize(),
                        filterReferenceRecords), listRecordRowMapper);
    }

    @Override
    public Integer countFilterModel(String searchValue, Boolean isGlobal) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(P2P_F_REFERENCE)
                .where(referenceFullFieldSearchCondition(searchValue, isGlobal));
        return fetchOne(where, Integer.class);
    }

    private Condition referenceFullFieldSearchCondition(String searchValue, Boolean isGlobal) {
        return appendConditions(StringUtils.isEmpty(searchValue) ? DSL.trueCondition() : DSL.falseCondition(),
                Operator.OR,
                new ConditionParameterSource()
                        .addValue(P2P_F_REFERENCE.ID, searchValue, LIKE)
                        .addValue(P2P_F_REFERENCE.TEMPLATE_ID, searchValue, LIKE)
                        .addValue(P2P_F_REFERENCE.IDENTITY_ID, searchValue, LIKE))
                .and(appendConditions(DSL.trueCondition(), Operator.AND,
                        new ConditionParameterSource()
                                .addValue(P2P_F_REFERENCE.IS_GLOBAL, isGlobal, EQUALS)));
    }
}
