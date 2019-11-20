package com.rbkmoney.fraudbusters.management.dao.p2p;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

@ContextConfiguration(classes = {P2pReferenceDaoImpl.class})
public class P2pReferenceDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String IDENTITY_ID = "identity_id";

    @Autowired
    P2pReferenceDao p2pReferenceDao;

    @Test
    public void insert() {
        String id = "id";
        P2pReferenceModel referenceModel = createReference(id);

        p2pReferenceDao.insert(referenceModel);

        P2pReferenceModel byId = p2pReferenceDao.getById(id);
        Assert.assertEquals(referenceModel, byId);

        p2pReferenceDao.remove(referenceModel);

        byId = p2pReferenceDao.getById(id);
        Assert.assertNull(byId);
    }

    @NotNull
    private P2pReferenceModel createReference(String id) {
        P2pReferenceModel referenceModel = new P2pReferenceModel();
        referenceModel.setId(id);
        String templateId = "template_id";
        String shopId = IDENTITY_ID;
        referenceModel.setTemplateId(templateId);
        referenceModel.setIdentityId(shopId);
        referenceModel.setIsGlobal(false);
        return referenceModel;
    }

    @Test
    public void constraintTest() {
        String id = "id";
        P2pReferenceModel referenceModel = createReference(id);

        p2pReferenceDao.insert(referenceModel);

        String test = "test";
        referenceModel.setTemplateId(test);
        p2pReferenceDao.insert(referenceModel);

        P2pReferenceModel byId = p2pReferenceDao.getById(id);
        Assert.assertEquals(byId.getTemplateId(), test);

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
        Assert.assertEquals(byId.getTemplateId(), global);

        byId = p2pReferenceDao.getById(firstGlobal);
        Assert.assertNull(byId);

        List<P2pReferenceModel> listByTFilters = p2pReferenceDao.getListByTFilters(IDENTITY_ID, null, 10);

        Assert.assertEquals(2, listByTFilters.size());

        listByTFilters = p2pReferenceDao.getListByTFilters(null, true, 10);

        Assert.assertEquals(1, listByTFilters.size());
    }
}