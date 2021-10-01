package com.rbkmoney.fraudbusters.management.dao.p2p;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import org.jooq.DSLContext;
import org.jooq.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFReference.P2P_F_REFERENCE;
import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {P2pReferenceDaoImpl.class})
public class P2pReferenceDaoImplTest {

    public static final String IDENTITY_ID = "identity_id";
    public static final String SECOND = "second_";
    public static final String THIRD = "third_";
    public static final String TEMPLATE_ID = "template_id";

    @Autowired
    P2pReferenceDao p2pReferenceDao;

    @Autowired
    private DSLContext dslContext;

    @AfterEach
    void cleanup() {
        dslContext.deleteFrom(P2P_F_REFERENCE).execute();
    }

    @Test
    void insert() {
        String id = "id";
        P2pReferenceModel referenceModel = createReference(id);

        p2pReferenceDao.insert(referenceModel);

        P2pReferenceModel byId = p2pReferenceDao.getById(id);
        assertEquals(referenceModel.getId(), byId.getId());

        p2pReferenceDao.remove(referenceModel);

        byId = p2pReferenceDao.getById(id);
        assertNull(byId);
    }

    private P2pReferenceModel createReference(String id) {
        P2pReferenceModel referenceModel = new P2pReferenceModel();
        referenceModel.setId(id);
        referenceModel.setTemplateId(TEMPLATE_ID);
        referenceModel.setIdentityId(IDENTITY_ID);
        referenceModel.setIsGlobal(false);
        return referenceModel;
    }

    @Test
    void constraintTest() {
        String id = "id";
        P2pReferenceModel referenceModel = createReference(id);

        p2pReferenceDao.insert(referenceModel);

        String test = "test";
        referenceModel.setTemplateId(test);
        p2pReferenceDao.insert(referenceModel);

        P2pReferenceModel byId = p2pReferenceDao.getById(id);
        assertEquals(byId.getTemplateId(), test);

        String firstGlobal = UUID.randomUUID().toString();
        referenceModel.setId(firstGlobal);
        referenceModel.setIsGlobal(true);
        p2pReferenceDao.insert(referenceModel);

        String global = "global";
        String globalId = UUID.randomUUID().toString();
        referenceModel.setId(globalId);
        referenceModel.setTemplateId(global);
        referenceModel.setIsGlobal(true);
        p2pReferenceDao.insert(referenceModel);

        byId = p2pReferenceDao.getById(globalId);
        assertEquals(byId.getTemplateId(), global);

        byId = p2pReferenceDao.getById(firstGlobal);
        assertNull(byId);

        List<P2pReferenceModel> listByTFilters = p2pReferenceDao.getListByTFilters(IDENTITY_ID, null, 10);

        assertEquals(2, listByTFilters.size());

        listByTFilters = p2pReferenceDao.getListByTFilters(null, true, 10);

        assertEquals(1, listByTFilters.size());
    }

    @Test
    void filterTest() {
        String id = "filter_id";
        P2pReferenceModel referenceModel = createReference(id);
        p2pReferenceDao.insert(referenceModel);

        referenceModel.setId(SECOND + id);
        referenceModel.setIdentityId(SECOND + IDENTITY_ID);
        p2pReferenceDao.insert(referenceModel);

        referenceModel.setId(THIRD + id);
        referenceModel.setIdentityId(THIRD + IDENTITY_ID);
        referenceModel.setTemplateId(THIRD + TEMPLATE_ID);
        referenceModel.setIsGlobal(true);
        p2pReferenceDao.insert(referenceModel);

        List<P2pReferenceModel> paymentReferenceModels = p2pReferenceDao.filterReferences(new FilterRequest(
                null,
                null,
                null,
                5,
                null,
                null), false);
        System.out.println(paymentReferenceModels);
        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(3, paymentReferenceModels.size());

        //check template field
        paymentReferenceModels = p2pReferenceDao.filterReferences(new FilterRequest(
                TEMPLATE_ID,
                null,
                null,
                5,
                null,
                null), false);

        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(2, paymentReferenceModels.size());

        //check regexp
        paymentReferenceModels = p2pReferenceDao.filterReferences(new FilterRequest(
                "%" + TEMPLATE_ID + "%",
                null,
                null,
                5,
                null,
                null), false);
        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(3, paymentReferenceModels.size());

        //check concrete
        paymentReferenceModels = p2pReferenceDao.filterReferences(new FilterRequest(
                THIRD + TEMPLATE_ID,
                null,
                null,
                5,
                null,
                null), false);
        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(1, paymentReferenceModels.size());

        //check sort
        paymentReferenceModels = p2pReferenceDao.filterReferences(new FilterRequest(
                null,
                null,
                null,
                5,
                "template_id",
                null), false);
        assertEquals(SECOND + id, paymentReferenceModels.get(0).getId());

        paymentReferenceModels = p2pReferenceDao.filterReferences(new FilterRequest(
                null,
                null,
                null,
                5,
                "template_id",
                SortOrder.DESC), false);
        assertEquals(THIRD + id, paymentReferenceModels.get(0).getId());

        paymentReferenceModels
                .forEach(paymentReferenceModel -> p2pReferenceDao.remove(paymentReferenceModel));
    }
}
