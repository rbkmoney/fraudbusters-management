package com.rbkmoney.fraudbusters.management.dao.p2p.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFGroupReferenceRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FGroupReference.F_GROUP_REFERENCE;
import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFGroupReference.P2P_F_GROUP_REFERENCE;

@Component
public class P2pGroupReferenceDaoImpl extends AbstractDao implements P2pGroupReferenceDao {

    private final RowMapper<P2pGroupReferenceModel> listRecordRowMapper;

    public P2pGroupReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(P2P_F_GROUP_REFERENCE, P2pGroupReferenceModel.class);
    }

    @Override
    public void insert(P2pGroupReferenceModel referenceModel) {
        Query query = getDslContext().insertInto(P2P_F_GROUP_REFERENCE)
                .set(getDslContext().newRecord(P2P_F_GROUP_REFERENCE, referenceModel))
                .onConflict(P2P_F_GROUP_REFERENCE.IDENTITY_ID)
                .doUpdate()
                .set(getDslContext().newRecord(F_GROUP_REFERENCE, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String identityId) {
        execute(getDslContext()
                .delete(P2P_F_GROUP_REFERENCE)
                .where(P2P_F_GROUP_REFERENCE.IDENTITY_ID.eq(identityId))
        );
    }

    @Override
    public void remove(P2pGroupReferenceModel referenceModel) {
        execute(getDslContext()
                .delete(P2P_F_GROUP_REFERENCE)
                .where(P2P_F_GROUP_REFERENCE.IDENTITY_ID.eq(referenceModel.getIdentityId()))
        );
    }

    @Override
    public List<P2pGroupReferenceModel> getByGroupId(String id) {
        SelectConditionStep<P2pFGroupReferenceRecord> where = getDslContext()
                .selectFrom(P2P_F_GROUP_REFERENCE)
                .where(P2P_F_GROUP_REFERENCE.GROUP_ID.eq(id));
        return fetch(where, listRecordRowMapper);
    }

    @Override
    public List<P2pGroupReferenceModel> getByIdentityId(String identityId) {
        SelectConditionStep<P2pFGroupReferenceRecord> where = getDslContext()
                .selectFrom(P2P_F_GROUP_REFERENCE)
                .where(P2P_F_GROUP_REFERENCE.IDENTITY_ID.eq(identityId));
        return fetch(where, listRecordRowMapper);
    }

    @Override
    public List<P2pGroupReferenceModel> filterReference(FilterRequest filterRequest) {
        SelectWhereStep<P2pFGroupReferenceRecord> from = getDslContext().selectFrom(P2P_F_GROUP_REFERENCE);
        Field<String> field = StringUtils.isEmpty(filterRequest.getSortBy()) ? P2P_F_GROUP_REFERENCE.GROUP_ID :
                P2P_F_GROUP_REFERENCE.field(filterRequest.getSortBy(), String.class);
        SelectConditionStep<P2pFGroupReferenceRecord> whereQuery = StringUtils.isEmpty(filterRequest.getSearchValue())
                ? from.where(DSL.trueCondition())
                : from.where(P2P_F_GROUP_REFERENCE.GROUP_ID.like(filterRequest.getSearchValue())
                        .or(P2P_F_GROUP_REFERENCE.IDENTITY_ID.like(filterRequest.getSearchValue())));
        SelectSeekStep2<P2pFGroupReferenceRecord, String, Long> filterGroupReferenceRecords =
                addSortCondition(P2P_F_GROUP_REFERENCE.ID,
                        field, filterRequest.getSortOrder(), whereQuery);
        return fetch(
                addSeekIfNeed(
                        parseIfExists(
                                filterRequest.getLastId()
                        ),
                        filterRequest.getSortFieldValue(),
                        filterRequest.getSize(),
                        filterGroupReferenceRecords),
                listRecordRowMapper
        );
    }

    private Long parseIfExists(String lastId) {
        return lastId != null ? Long.valueOf(lastId) : null;
    }

    @Override
    public Integer countFilterReference(String filterValue) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(P2P_F_GROUP_REFERENCE)
                .where(!StringUtils.isEmpty(filterValue)
                        ? P2P_F_GROUP_REFERENCE.GROUP_ID.like(filterValue)
                                .or(P2P_F_GROUP_REFERENCE.IDENTITY_ID.like(filterValue))
                        : DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

}
