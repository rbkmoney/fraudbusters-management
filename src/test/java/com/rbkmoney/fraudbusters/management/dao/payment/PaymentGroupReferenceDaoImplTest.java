package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.group.GroupReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FGroupReferenceRecord;
import com.rbkmoney.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.FGroupReference.F_GROUP_REFERENCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JooqTest
@PostgresqlTestcontainerSingleton
@ContextConfiguration(classes = {GroupReferenceDaoImpl.class})
public class PaymentGroupReferenceDaoImplTest {

    public static final String GROUP_ID = "groupId";
    public static final String PARTY_ID = "party_id";

    @Autowired
    GroupReferenceDaoImpl groupReferenceDao;

    @Autowired
    DSLContext dslContext;

    @Test
    void getByGroupId() {
        PaymentGroupReferenceModel referenceModel = new PaymentGroupReferenceModel();
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setGroupId(GROUP_ID);

        dslContext.insertInto(F_GROUP_REFERENCE)
                .set(dslContext.newRecord(F_GROUP_REFERENCE, referenceModel))
                .execute();

        List<PaymentGroupReferenceModel> paymentGroupReferenceModels = groupReferenceDao.getByGroupId(GROUP_ID);

        assertEquals(PARTY_ID, paymentGroupReferenceModels.get(0).getPartyId());
        ;
    }

    @Test
    void insert() {
        PaymentGroupReferenceModel referenceModel = new PaymentGroupReferenceModel();
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setGroupId(GROUP_ID);

        groupReferenceDao.insert(referenceModel);

        FGroupReferenceRecord fGroupReferenceRecord = dslContext.fetchAny(F_GROUP_REFERENCE);
        assertEquals(PARTY_ID, fGroupReferenceRecord.getPartyId());
        ;
    }

    @Test
    void remove() {
        PaymentGroupReferenceModel referenceModel = new PaymentGroupReferenceModel();
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setGroupId(GROUP_ID);
        dslContext.insertInto(F_GROUP_REFERENCE)
                .set(dslContext.newRecord(F_GROUP_REFERENCE, referenceModel))
                .execute();

        groupReferenceDao.remove(referenceModel);

        Result<FGroupReferenceRecord> referenceRecords = dslContext
                .selectFrom(F_GROUP_REFERENCE)
                .where(F_GROUP_REFERENCE.GROUP_ID.eq(GROUP_ID))
                .fetch();

        assertTrue(referenceRecords.isEmpty());
    }
}
