package com.rbkmoney.fraudbusters.management.dao.reference;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

@ContextConfiguration(classes = {ReferenceDaoImpl.class})
public class ReferenceDaoImplTest extends AbstractPostgresIntegrationTest {

    @Autowired
    ReferenceDao referenceDao;

    @Test
    public void insert() {
        String id = "id";
        ReferenceModel referenceModel = createReference(id);

        referenceDao.insert(referenceModel);

        ReferenceModel byId = referenceDao.getById(id);
        Assert.assertEquals(referenceModel, byId);

        referenceDao.remove(referenceModel);

        byId = referenceDao.getById(id);
        Assert.assertNull(byId);
    }

    @NotNull
    private ReferenceModel createReference(String id) {
        ReferenceModel referenceModel = new ReferenceModel();
        referenceModel.setId(id);
        String templateId = "template_id";
        String shopId = "shop_id";
        String partyId = "party_id";
        referenceModel.setTemplateId(templateId);
        referenceModel.setShopId(shopId);
        referenceModel.setPartyId(partyId);
        referenceModel.setIsGlobal(false);
        return referenceModel;
    }

    @Test
    public void constraintTest() {
        String id = "id";
        ReferenceModel referenceModel = createReference(id);

        referenceDao.insert(referenceModel);

        String test = "test";
        referenceModel.setTemplateId(test);
        referenceDao.insert(referenceModel);

        ReferenceModel byId = referenceDao.getById(id);
        Assert.assertEquals(byId.getTemplateId(), test);

        String firstGlobal = UUID.randomUUID().toString();
        referenceModel.setId(firstGlobal);
        referenceModel.setIsGlobal(true);
        referenceDao.insert(referenceModel);

        String global = "global";
        String globalId = UUID.randomUUID().toString();
        referenceModel.setId(globalId);
        referenceModel.setTemplateId(global);
        referenceModel.setIsGlobal(true);
        referenceDao.insert(referenceModel);

        byId = referenceDao.getById(globalId);
        Assert.assertEquals(byId.getTemplateId(), global);

        byId = referenceDao.getById(firstGlobal);
        Assert.assertNull(byId);
    }

    @Test
    public void getById() {

    }
}