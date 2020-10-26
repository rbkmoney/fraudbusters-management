package com.rbkmoney.fraudbusters.management.dao.payment.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.condition.ConditionParameterSource;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FGroupReferenceRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FGroupReference.F_GROUP_REFERENCE;
import static org.jooq.Comparator.EQUALS;

@Component
public class GroupReferenceDaoImpl extends AbstractDao implements PaymentGroupReferenceDao {

    private final RowMapper<PaymentGroupReferenceModel> listRecordRowMapper;

    public GroupReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_GROUP_REFERENCE, PaymentGroupReferenceModel.class);
    }

    @Override
    public void insert(PaymentGroupReferenceModel referenceModel) {
        Query query = getDslContext().insertInto(F_GROUP_REFERENCE)
                .set(getDslContext().newRecord(F_GROUP_REFERENCE, referenceModel))
                .onConflict(F_GROUP_REFERENCE.PARTY_ID, F_GROUP_REFERENCE.SHOP_ID)
                .doUpdate()
                .set(getDslContext().newRecord(F_GROUP_REFERENCE, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String partyId, String shopId) {
        execute(getDslContext()
                .delete(F_GROUP_REFERENCE)
                .where(F_GROUP_REFERENCE.PARTY_ID.eq(partyId)
                        .and(F_GROUP_REFERENCE.SHOP_ID.eq(shopId))));
    }

    @Override
    public void remove(PaymentGroupReferenceModel referenceModel) {
        Condition condition = DSL.trueCondition();
        execute(getDslContext()
                .delete(F_GROUP_REFERENCE)
                .where(appendConditions(condition, Operator.AND,
                        new ConditionParameterSource()
                                .addValue(F_GROUP_REFERENCE.PARTY_ID, referenceModel.getPartyId(), EQUALS)
                                .addValue(F_GROUP_REFERENCE.SHOP_ID, referenceModel.getShopId(), EQUALS))));
    }

    @Override
    public List<PaymentGroupReferenceModel> getByGroupId(String id) {
        SelectConditionStep<Record4<Long, String, String, String>> where =
                getDslContext()
                        .select(F_GROUP_REFERENCE.ID,
                                F_GROUP_REFERENCE.PARTY_ID,
                                F_GROUP_REFERENCE.SHOP_ID,
                                F_GROUP_REFERENCE.GROUP_ID)
                        .from(F_GROUP_REFERENCE)
                        .where(F_GROUP_REFERENCE.GROUP_ID.eq(id));
        return fetch(where, listRecordRowMapper);
    }

    @Override
    public List<PaymentGroupReferenceModel> filterReference(String filterValue, String lastId, String sortFieldValue,
                                                            Integer size, String sortingBy, SortOrder sortOrder) {
        SelectWhereStep<FGroupReferenceRecord> from = getDslContext()
                .selectFrom(F_GROUP_REFERENCE);
        Field<String> field = StringUtils.isEmpty(sortingBy) ? F_GROUP_REFERENCE.GROUP_ID : F_GROUP_REFERENCE.field(sortingBy, String.class);
        SelectConditionStep<FGroupReferenceRecord> whereQuery = StringUtils.isEmpty(filterValue) ?
                from.where(DSL.trueCondition()) : from.where(F_GROUP_REFERENCE.GROUP_ID.like(filterValue)
                .or(F_GROUP_REFERENCE.PARTY_ID.like(filterValue)
                        .or(F_GROUP_REFERENCE.SHOP_ID.like(filterValue))));
        SelectSeekStep2<FGroupReferenceRecord, Long, String> fGroupReferenceRecords = addSortCondition(F_GROUP_REFERENCE.ID, field, sortOrder, whereQuery);
        return fetch(addSeekIfNeed(parseIfExists(lastId), sortFieldValue, size, fGroupReferenceRecords), listRecordRowMapper);
    }

    private Long parseIfExists(String lastId) {
        return lastId != null ? Long.valueOf(lastId) : null;
    }

    @Override
    public List<PaymentGroupReferenceModel> getByPartyIdAndShopId(String partyId, String shopId) {
        SelectConditionStep<Record4<Long, String, String, String>> where =
                getDslContext()
                        .select(F_GROUP_REFERENCE.ID,
                                F_GROUP_REFERENCE.PARTY_ID,
                                F_GROUP_REFERENCE.SHOP_ID,
                                F_GROUP_REFERENCE.GROUP_ID)
                        .from(F_GROUP_REFERENCE)
                        .where(F_GROUP_REFERENCE.PARTY_ID.eq(partyId)
                                .and((F_GROUP_REFERENCE.SHOP_ID.eq(shopId)
                                        .or(F_GROUP_REFERENCE.SHOP_ID.isNull()))));
        return fetch(where, listRecordRowMapper);
    }
}
