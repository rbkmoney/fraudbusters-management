package com.rbkmoney.fraudbusters.management.dao.payment.reference;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FReferenceRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FReference.F_REFERENCE;
import static org.jooq.Comparator.EQUALS;

@Component
public class ReferenceDaoImpl extends AbstractDao implements ReferenceDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<ReferenceModel> listRecordRowMapper;

    public ReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_REFERENCE, ReferenceModel.class);
    }

    @Override
    public void insert(ReferenceModel referenceModel) {
        Query query = getDslContext().insertInto(F_REFERENCE)
                .set(getDslContext().newRecord(F_REFERENCE, referenceModel))
                .onConflict(F_REFERENCE.PARTY_ID, F_REFERENCE.SHOP_ID, F_REFERENCE.IS_GLOBAL)
                .doUpdate()
                .set(getDslContext().newRecord(F_REFERENCE, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<FReferenceRecord> where = getDslContext()
                .delete(F_REFERENCE)
                .where(F_REFERENCE.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(ReferenceModel referenceModel) {
        Condition condition = DSL.trueCondition();
        DeleteConditionStep<FReferenceRecord> where = getDslContext()
                .delete(F_REFERENCE)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(F_REFERENCE.PARTY_ID, referenceModel.getPartyId(), EQUALS)
                                .addValue(F_REFERENCE.SHOP_ID, referenceModel.getShopId(), EQUALS)
                                .addValue(F_REFERENCE.IS_GLOBAL, referenceModel.getIsGlobal(), EQUALS)));
        execute(where);
    }

    @Override
    public ReferenceModel getById(String id) {
        SelectConditionStep<Record5<String, String, String, String, Boolean>> where =
                getDslContext()
                        .select(F_REFERENCE.ID,
                                F_REFERENCE.PARTY_ID,
                                F_REFERENCE.SHOP_ID,
                                F_REFERENCE.TEMPLATE_ID,
                                F_REFERENCE.IS_GLOBAL)
                        .from(F_REFERENCE)
                        .where(F_REFERENCE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<ReferenceModel> getList(int limit) {
        SelectLimitPercentStep<Record5<String, String, String, String, Boolean>> query =
                getDslContext()
                        .select(F_REFERENCE.ID,
                                F_REFERENCE.PARTY_ID,
                                F_REFERENCE.SHOP_ID,
                                F_REFERENCE.TEMPLATE_ID,
                                F_REFERENCE.IS_GLOBAL)
                        .from(F_REFERENCE)
                        .limit(limit != 0 ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public List<ReferenceModel> getListByTemplateId(String templateId, int limit) {
        SelectLimitPercentStep<Record5<String, String, String, String, Boolean>> query =
                getDslContext()
                        .select(F_REFERENCE.ID,
                                F_REFERENCE.PARTY_ID,
                                F_REFERENCE.SHOP_ID,
                                F_REFERENCE.TEMPLATE_ID,
                                F_REFERENCE.IS_GLOBAL)
                        .from(F_REFERENCE)
                        .where(F_REFERENCE.TEMPLATE_ID.eq(templateId))
                        .limit(limit != 0 ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public List<ReferenceModel> getListByTFilters(String partyId, String shopId, Boolean isGlobal, int limit) {
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep<Record5<String, String, String, String, Boolean>> query =
                getDslContext()
                        .select(F_REFERENCE.ID,
                                F_REFERENCE.PARTY_ID,
                                F_REFERENCE.SHOP_ID,
                                F_REFERENCE.TEMPLATE_ID,
                                F_REFERENCE.IS_GLOBAL)
                        .from(F_REFERENCE)
                        .where(appendConditions(condition, Operator.AND,
                                new ConditionParameterSource()
                                        .addValue(F_REFERENCE.PARTY_ID, partyId, EQUALS)
                                        .addValue(F_REFERENCE.SHOP_ID, shopId, EQUALS)
                                        .addValue(F_REFERENCE.IS_GLOBAL, isGlobal, EQUALS)))
                        .limit(limit != 0 ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public ReferenceModel getGlobalReference() {
        return fetchOne(getDslContext()
                        .select(F_REFERENCE.ID,
                                F_REFERENCE.PARTY_ID,
                                F_REFERENCE.SHOP_ID,
                                F_REFERENCE.TEMPLATE_ID,
                                F_REFERENCE.IS_GLOBAL)
                        .from(F_REFERENCE)
                        .where(F_REFERENCE.IS_GLOBAL.eq(true)),
                listRecordRowMapper);
    }


    @Override
    public List<ReferenceModel> getByPartyAndShop(String partyId, String shopId) {
        SelectConditionStep<Record5<String, String, String, String, Boolean>> where = getDslContext()
                .select(F_REFERENCE.ID,
                        F_REFERENCE.PARTY_ID,
                        F_REFERENCE.SHOP_ID,
                        F_REFERENCE.TEMPLATE_ID,
                        F_REFERENCE.IS_GLOBAL)
                .from(F_REFERENCE)
                .where(F_REFERENCE.PARTY_ID.eq(partyId)
                        .and(F_REFERENCE.SHOP_ID.eq(shopId).or(F_REFERENCE.SHOP_ID.isNull()))
                        .and(F_REFERENCE.IS_GLOBAL.eq(false)));
        return fetch(where, listRecordRowMapper);
    }
}
