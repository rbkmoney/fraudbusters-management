package com.rbkmoney.fraudbusters.management.dao.p2p;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.DefaultReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.DefaultP2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFDefaultRefRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.Tables.F_DEFAULT_REF;
import static com.rbkmoney.fraudbusters.management.domain.Tables.P2P_F_DEFAULT_REF;

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
    public DefaultP2pReferenceModel getById(String id) {
        Query query = getDslContext()
                .selectFrom(P2P_F_DEFAULT_REF)
                .where(P2P_F_DEFAULT_REF.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }


    @Override
    public List<DefaultP2pReferenceModel> filterReferences(FilterRequest filterRequest) {
        SelectWhereStep<P2pFDefaultRefRecord> from = getDslContext()
                .selectFrom(P2P_F_DEFAULT_REF);
        Field<String> field = StringUtils.isEmpty(filterRequest.getSortBy()) ? P2P_F_DEFAULT_REF.TEMPLATE_ID : P2P_F_DEFAULT_REF.field(filterRequest.getSortBy(), String.class);
        SelectConditionStep<P2pFDefaultRefRecord> whereQuery = StringUtils.isEmpty(filterRequest.getSearchValue()) ?
                from.where(DSL.trueCondition()) :
                from.where(P2P_F_DEFAULT_REF.TEMPLATE_ID.like(filterRequest.getSearchValue())
                        .or(P2P_F_DEFAULT_REF.IDENTITY_ID.like(filterRequest.getSearchValue())));
        SelectSeekStep2<P2pFDefaultRefRecord, String, String> fReferenceRecords = addSortCondition(
                P2P_F_DEFAULT_REF.ID, field, filterRequest.getSortOrder(), whereQuery);
        return fetch(addSeekIfNeed(
                filterRequest.getLastId(),
                filterRequest.getSortFieldValue(),
                filterRequest.getSize(),
                fReferenceRecords),
                listRecordRowMapper);
    }

    @Override
    public Integer countFilterModel(String filterValue) {
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(P2P_F_DEFAULT_REF)
                .where(!StringUtils.isEmpty(filterValue) ?
                        P2P_F_DEFAULT_REF.TEMPLATE_ID.like(filterValue)
                                .or(P2P_F_DEFAULT_REF.IDENTITY_ID.like(filterValue)) :
                        DSL.noCondition());
        return fetchOne(where, Integer.class);
    }

    public DefaultP2pReferenceModel getByIdentityId(String identityId) {
        Query query = getDslContext()
                .selectFrom(F_DEFAULT_REF)
                .where(identityId == null ? P2P_F_DEFAULT_REF.IDENTITY_ID.isNull() :
                        P2P_F_DEFAULT_REF.IDENTITY_ID.eq(identityId));
        return fetchOne(query, listRecordRowMapper);
    }
}
