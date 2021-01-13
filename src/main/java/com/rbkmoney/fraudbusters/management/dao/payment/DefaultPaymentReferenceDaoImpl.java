package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.DefaultReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FDefaultRefRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.Condition;
import org.jooq.DeleteConditionStep;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fraudbusters.management.domain.Tables.F_DEFAULT_REF;
import static org.jooq.Comparator.EQUALS;

@Component
public class DefaultPaymentReferenceDaoImpl extends AbstractDao implements DefaultReferenceDao<DefaultPaymentReferenceModel> {

    private final RowMapper<DefaultPaymentReferenceModel> listRecordRowMapper;

    public DefaultPaymentReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_DEFAULT_REF, DefaultPaymentReferenceModel.class);
    }

    @Override
    public void insert(DefaultPaymentReferenceModel referenceModel) {
        referenceModel.setLastUpdateDate(null);
        Query query = getDslContext().insertInto(F_DEFAULT_REF)
                .set(getDslContext().newRecord(F_DEFAULT_REF, referenceModel))
                .onConflict(F_DEFAULT_REF.PARTY_ID, F_DEFAULT_REF.SHOP_ID)
                .doUpdate()
                .set(getDslContext().newRecord(F_DEFAULT_REF, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<FDefaultRefRecord> where = getDslContext()
                .delete(F_DEFAULT_REF)
                .where(F_DEFAULT_REF.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(DefaultPaymentReferenceModel referenceModel) {
        Condition condition = DSL.trueCondition();
        DeleteConditionStep<FDefaultRefRecord> where = getDslContext()
                .delete(F_DEFAULT_REF)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(F_DEFAULT_REF.PARTY_ID, referenceModel.getPartyId(), EQUALS)
                                .addValue(F_DEFAULT_REF.SHOP_ID, referenceModel.getShopId(), EQUALS)));
        execute(where);
    }

    @Override
    public DefaultPaymentReferenceModel getById(String id) {
        Query query = getDslContext()
                .selectFrom(F_DEFAULT_REF)
                .where(F_DEFAULT_REF.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    public DefaultPaymentReferenceModel getByPartyAndShop(String partyId, String shopId) {
        Condition condition = DSL.trueCondition();
        Query query = getDslContext()
                .selectFrom(F_DEFAULT_REF)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(F_DEFAULT_REF.PARTY_ID, partyId, EQUALS)
                                .addValue(F_DEFAULT_REF.SHOP_ID, shopId, EQUALS)));
        return fetchOne(query, listRecordRowMapper);
    }
}
