package com.rbkmoney.fraudbusters.management.dao.p2p.reference;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFReferenceRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFReference.P2P_F_REFERENCE;
import static org.jooq.Comparator.EQUALS;

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
                                .addValue(P2P_F_REFERENCE.IDENTITY_ID, referenceModel.getIdentityId(), EQUALS)
                                .addValue(P2P_F_REFERENCE.IS_GLOBAL, referenceModel.getIsGlobal(), EQUALS)));
        execute(where);
    }

    @Override
    public P2pReferenceModel getById(String id) {
        SelectConditionStep<Record4<String, String, String, Boolean>> where =
                getDslContext()
                        .select(P2P_F_REFERENCE.ID,
                                P2P_F_REFERENCE.IDENTITY_ID,
                                P2P_F_REFERENCE.TEMPLATE_ID,
                                P2P_F_REFERENCE.IS_GLOBAL)
                        .from(P2P_F_REFERENCE)
                        .where(P2P_F_REFERENCE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<P2pReferenceModel> getList(Integer limit) {
        SelectLimitPercentStep<Record4<String, String, String, Boolean>> query =
                getDslContext()
                        .select(P2P_F_REFERENCE.ID,
                                P2P_F_REFERENCE.IDENTITY_ID,
                                P2P_F_REFERENCE.TEMPLATE_ID,
                                P2P_F_REFERENCE.IS_GLOBAL)
                        .from(P2P_F_REFERENCE)
                        .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public List<P2pReferenceModel> getListByTemplateId(String templateId, Integer limit) {
        SelectLimitPercentStep<Record4<String, String, String, Boolean>> query =
                getDslContext()
                        .select(P2P_F_REFERENCE.ID,
                                P2P_F_REFERENCE.IDENTITY_ID,
                                P2P_F_REFERENCE.TEMPLATE_ID,
                                P2P_F_REFERENCE.IS_GLOBAL)
                        .from(P2P_F_REFERENCE)
                        .where(P2P_F_REFERENCE.TEMPLATE_ID.eq(templateId))
                        .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public List<P2pReferenceModel> getListByTFilters(String identityId, Boolean isGlobal, Integer limit) {
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep<Record4<String, String, String, Boolean>> query =
                getDslContext()
                        .select(P2P_F_REFERENCE.ID,
                                P2P_F_REFERENCE.IDENTITY_ID,
                                P2P_F_REFERENCE.TEMPLATE_ID,
                                P2P_F_REFERENCE.IS_GLOBAL)
                        .from(P2P_F_REFERENCE)
                        .where(appendConditions(condition, Operator.AND,
                                new ConditionParameterSource()
                                        .addValue(P2P_F_REFERENCE.IDENTITY_ID, identityId, EQUALS)
                                        .addValue(P2P_F_REFERENCE.IS_GLOBAL, isGlobal, EQUALS)))
                        .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public P2pReferenceModel getGlobalReference() {
        return fetchOne(getDslContext()
                        .select(P2P_F_REFERENCE.ID,
                                P2P_F_REFERENCE.IDENTITY_ID,
                                P2P_F_REFERENCE.TEMPLATE_ID,
                                P2P_F_REFERENCE.IS_GLOBAL)
                        .from(P2P_F_REFERENCE)
                        .where(P2P_F_REFERENCE.IS_GLOBAL.eq(true)),
                listRecordRowMapper);
    }


    @Override
    public List<P2pReferenceModel> getByIdentity(String identityId) {
        SelectConditionStep<Record4<String, String, String, Boolean>> where = getDslContext()
                .select(P2P_F_REFERENCE.ID,
                        P2P_F_REFERENCE.IDENTITY_ID,
                        P2P_F_REFERENCE.TEMPLATE_ID,
                        P2P_F_REFERENCE.IS_GLOBAL)
                .from(P2P_F_REFERENCE)
                .where(P2P_F_REFERENCE.IDENTITY_ID.eq(identityId)
                        .and(P2P_F_REFERENCE.IS_GLOBAL.eq(false))
                );
        return fetch(where, listRecordRowMapper);
    }
}
