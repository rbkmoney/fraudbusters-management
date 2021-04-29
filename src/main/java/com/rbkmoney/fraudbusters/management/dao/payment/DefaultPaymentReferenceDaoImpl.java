package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.DefaultReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FDefaultRefRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static com.rbkmoney.fraudbusters.management.domain.Tables.F_DEFAULT_REF;

@Component
public class DefaultPaymentReferenceDaoImpl extends AbstractDao
        implements DefaultReferenceDao<DefaultPaymentReferenceModel> {

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
    public DefaultPaymentReferenceModel getById(String id) {
        Query query = getDslContext()
                .selectFrom(F_DEFAULT_REF)
                .where(F_DEFAULT_REF.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<DefaultPaymentReferenceModel> filterReferences(FilterRequest filterRequest) {
        SelectWhereStep<FDefaultRefRecord> from = getDslContext()
                .selectFrom(F_DEFAULT_REF);
        Field<String> field = StringUtils.isEmpty(filterRequest.getSortBy())
                ? F_DEFAULT_REF.TEMPLATE_ID
                : F_DEFAULT_REF.field(filterRequest.getSortBy(), String.class);
        SelectConditionStep<FDefaultRefRecord> whereQuery = StringUtils.isEmpty(filterRequest.getSearchValue())
                ? from.where(DSL.trueCondition())
                : from.where(F_DEFAULT_REF.TEMPLATE_ID.like(filterRequest.getSearchValue())
                        .or(F_DEFAULT_REF.PARTY_ID.like(filterRequest.getSearchValue()))
                        .or(F_DEFAULT_REF.SHOP_ID.like(filterRequest.getSearchValue())));
        SelectSeekStep2<FDefaultRefRecord, String, String> filterReferenceRecords = addSortCondition(
                F_DEFAULT_REF.ID, field, filterRequest.getSortOrder(), whereQuery);
        return fetch(
                addSeekIfNeed(
                        filterRequest.getLastId(),
                        filterRequest.getSortFieldValue(),
                        filterRequest.getSize(),
                        filterReferenceRecords
                ),
                listRecordRowMapper
        );
    }

    @Override
    public Integer countFilterModel(String filterValue) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(F_DEFAULT_REF)
                .where(!StringUtils.isEmpty(filterValue)
                        ? F_DEFAULT_REF.TEMPLATE_ID.like(filterValue)
                                .or(F_DEFAULT_REF.PARTY_ID.like(filterValue)
                                        .or(F_DEFAULT_REF.SHOP_ID.like(filterValue)))
                        : DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

    public Optional<DefaultPaymentReferenceModel> getByPartyAndShop(String partyId, String shopId) {
        Query query = getDslContext()
                .selectFrom(F_DEFAULT_REF)
                .where((partyId == null ? F_DEFAULT_REF.PARTY_ID.isNull() : F_DEFAULT_REF.PARTY_ID.eq(partyId))
                        .and(shopId == null ? F_DEFAULT_REF.SHOP_ID.isNull() : F_DEFAULT_REF.SHOP_ID.eq(shopId)));
        return Optional.ofNullable(fetchOne(query, listRecordRowMapper));
    }
}
