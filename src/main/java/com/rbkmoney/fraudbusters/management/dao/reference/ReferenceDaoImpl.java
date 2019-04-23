package com.rbkmoney.fraudbusters.management.dao.reference;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.mapper.RecordRowMapper;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FReferenceRecord;
import com.rbkmoney.fraudbusters.management.exception.DaoException;
import org.jooq.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FReference.F_REFERENCE;

@Component
public class ReferenceDaoImpl extends AbstractDao implements ReferenceDao {

    private static final int LIMIT_TOTAL = 100;
    private final RowMapper<ReferenceModel> listRecordRowMapper;

    public ReferenceDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(F_REFERENCE, ReferenceModel.class);
    }

    @Override
    public void insert(ReferenceModel referenceModel) throws DaoException {
        Query query = getDslContext().insertInto(F_REFERENCE)
                .set(getDslContext().newRecord(F_REFERENCE, referenceModel))
                .onConflict(F_REFERENCE.PARTY_ID, F_REFERENCE.SHOP_ID, F_REFERENCE.IS_GLOBAL)
                .doUpdate()
                .set(getDslContext().newRecord(F_REFERENCE, referenceModel));
        execute(query);
    }

    @Override
    public void remove(String id) throws DaoException {
        DeleteConditionStep<FReferenceRecord> where = getDslContext()
                .delete(F_REFERENCE)
                .where(F_REFERENCE.ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(ReferenceModel referenceModel) throws DaoException {
        DeleteConditionStep<FReferenceRecord> where = getDslContext()
                .delete(F_REFERENCE)
                .where(F_REFERENCE.PARTY_ID.eq(referenceModel.getPartyId())
                        .and(F_REFERENCE.SHOP_ID.eq(referenceModel.getShopId())
                                .and(F_REFERENCE.IS_GLOBAL.eq(referenceModel.getIsGlobal()))));
        execute(where);
    }

    @Override
    public ReferenceModel getById(String id) throws DaoException {
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
    public List<ReferenceModel> getList(int limit) throws DaoException {
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
    public List<ReferenceModel> getListByTemplateId(String templateId, int limit) throws DaoException {
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
}
