package com.rbkmoney.fraudbusters.management.dao.p2p;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.DefaultReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.p2p.DefaultP2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFDefaultRefRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.Condition;
import org.jooq.DeleteConditionStep;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fraudbusters.management.domain.Tables.P2P_F_DEFAULT_REF;
import static org.jooq.Comparator.EQUALS;

@Component
public class DefaultP2pReferenceDaoImpl extends AbstractDao implements DefaultReferenceDao<DefaultP2pReferenceModel> {

    private final RowMapper<DefaultP2pReferenceModel> listRecordRowMapper;

    public DefaultP2pReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(P2P_F_DEFAULT_REF, DefaultP2pReferenceModel.class);
    }

    @Override
    public void insert(DefaultP2pReferenceModel referenceModel) {
        referenceModel.setLastUpdateDate(null);
        Query query = getDslContext().insertInto(P2P_F_DEFAULT_REF)
                .set(getDslContext().newRecord(P2P_F_DEFAULT_REF, referenceModel))
                .onConflict(P2P_F_DEFAULT_REF.IDENTITY_ID)
                .doUpdate()
                .set(getDslContext().newRecord(P2P_F_DEFAULT_REF, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<P2pFDefaultRefRecord> where = getDslContext()
                .delete(P2P_F_DEFAULT_REF)
                .where(P2P_F_DEFAULT_REF.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(DefaultP2pReferenceModel referenceModel) {
        Condition condition = DSL.trueCondition();
        DeleteConditionStep<P2pFDefaultRefRecord> where = getDslContext()
                .delete(P2P_F_DEFAULT_REF)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(P2P_F_DEFAULT_REF.IDENTITY_ID, referenceModel.getIdentityId(), EQUALS)));
        execute(where);
    }

    @Override
    public DefaultP2pReferenceModel getById(String id) {
        Query query = getDslContext()
                .selectFrom(P2P_F_DEFAULT_REF)
                .where(P2P_F_DEFAULT_REF.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }
}
