package com.rbkmoney.fraudbusters.management.dao.payment.reference;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FReferenceRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FReference.F_REFERENCE;
import static org.jooq.Comparator.EQUALS;

@Component
public class PaymentReferenceDaoImpl extends AbstractDao implements PaymentReferenceDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<PaymentReferenceModel> listRecordRowMapper;

    public PaymentReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_REFERENCE, PaymentReferenceModel.class);
    }

    @Override
    public void insert(PaymentReferenceModel referenceModel) {
        referenceModel.setLastUpdateDate(null);
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
    public void remove(PaymentReferenceModel referenceModel) {
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
    public PaymentReferenceModel getById(String id) {
        Query query = getDslContext()
                .selectFrom(F_REFERENCE)
                .where(F_REFERENCE.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<PaymentReferenceModel> getListByTFilters(String partyId, String shopId, Integer limit) {
        Condition condition = DSL.trueCondition();
        Query query = getDslContext().selectFrom(F_REFERENCE)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(F_REFERENCE.PARTY_ID, partyId, EQUALS)
                                .addValue(F_REFERENCE.SHOP_ID, shopId, EQUALS)))
                .limit(limit != null ? limit : LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public PaymentReferenceModel getGlobalReference() {
        Query query = getDslContext().selectFrom(F_REFERENCE)
                .where(F_REFERENCE.IS_GLOBAL.eq(true));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<PaymentReferenceModel> getByPartyAndShop(String partyId, String shopId) {
        Query query = getDslContext().selectFrom(F_REFERENCE)
                .where(F_REFERENCE.PARTY_ID.eq(partyId)
                        .and(F_REFERENCE.SHOP_ID.eq(shopId).or(F_REFERENCE.SHOP_ID.isNull()))
                        .and(F_REFERENCE.IS_GLOBAL.eq(false)));
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public Boolean isPartyShopReferenceExist(String partyId, String shopId) {
        Query query = getDslContext().selectFrom(F_REFERENCE)
                .where(F_REFERENCE.PARTY_ID.eq(partyId)
                        .and(F_REFERENCE.SHOP_ID.eq(shopId)))
                .and(F_REFERENCE.IS_GLOBAL.eq(false));
        return !CollectionUtils.isEmpty(fetch(query, listRecordRowMapper));
    }

    @Override
    public List<PaymentReferenceModel> filterReferences(FilterRequest filterRequest) {
        SelectWhereStep<FReferenceRecord> from = getDslContext()
                .selectFrom(F_REFERENCE);
        Field<String> field = StringUtils.isEmpty(filterRequest.getSortBy()) ? F_REFERENCE.TEMPLATE_ID :
                F_REFERENCE.field(filterRequest.getSortBy(), String.class);
        SelectConditionStep<FReferenceRecord> whereQuery = StringUtils.isEmpty(filterRequest.getSearchValue()) ?
                from.where(DSL.trueCondition()) : from.where(
                F_REFERENCE.TEMPLATE_ID.like(filterRequest.getSearchValue())
                        .or(F_REFERENCE.PARTY_ID.like(filterRequest.getSearchValue()))
                        .or(F_REFERENCE.SHOP_ID.like(filterRequest.getSearchValue())));
        SelectSeekStep2<FReferenceRecord, String, String> fReferenceRecords = addSortCondition(
                F_REFERENCE.ID, field, filterRequest.getSortOrder(), whereQuery);
        return fetch(
                addSeekIfNeed(
                        filterRequest.getLastId(),
                        filterRequest.getSortFieldValue(),
                        filterRequest.getSize(),
                        fReferenceRecords
                ),
                listRecordRowMapper
        );
    }

    @Override
    public Integer countFilterModel(String filterValue) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(F_REFERENCE)
                .where(!StringUtils.isEmpty(filterValue) ?
                        F_REFERENCE.TEMPLATE_ID.like(filterValue)
                                .or(F_REFERENCE.PARTY_ID.like(filterValue)
                                        .or(F_REFERENCE.SHOP_ID.like(filterValue))) :
                        DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

}
