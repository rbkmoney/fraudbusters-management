package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.group.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.group.GroupDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.group.GroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.group.GroupReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.ReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.ReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.TemplateDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = {EmulateResource.class, GroupDaoImpl.class, TemplateDaoImpl.class, GroupReferenceDaoImpl.class, ReferenceDaoImpl.class})
public class EmulateResourceTest extends AbstractPostgresIntegrationTest {

    private static final String PARTY_ID = "partyId";
    private static final String SHOP_ID = "shopId";
    private static final String GROUP_ID = "groupId";
    private static final String TEMPLATE_1 = "template_1";
    private static final String TEMPLATE_2 = "template_2";
    private static final String TEMPLATE_3 = "template_3";
    private static final String GLOBAL_TEMPLATE = "template_global";

    @Autowired
    EmulateResource emulateResource;
    @Autowired
    GroupReferenceDao groupReferenceDao;
    @Autowired
    GroupDao groupDao;
    @Autowired
    ReferenceDao referenceDao;
    @Autowired
    TemplateDao templateDao;

    @Test
    public void getRulesByPartyAndShop() {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId(GROUP_ID);
        ArrayList<PriorityIdModel> priorityTemplates = new ArrayList<>();
        priorityTemplates.add(new PriorityIdModel(2L, TEMPLATE_1));
        priorityTemplates.add(new PriorityIdModel(1L, TEMPLATE_2));
        groupModel.setPriorityTemplates(priorityTemplates);
        groupDao.insert(groupModel);

        GroupReferenceModel referenceModel = new GroupReferenceModel();
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setShopId(SHOP_ID);
        referenceModel.setGroupId(GROUP_ID);
        groupReferenceDao.insert(referenceModel);

        insertTemplate("test - " + TEMPLATE_1, TEMPLATE_1);
        insertTemplate("test - " + TEMPLATE_2, TEMPLATE_2);
        insertTemplate("test - " + TEMPLATE_3, TEMPLATE_3);
        insertTemplate("test - " + GLOBAL_TEMPLATE, GLOBAL_TEMPLATE);

        ReferenceModel referenceModel1 = new ReferenceModel();
        referenceModel1.setTemplateId(TEMPLATE_3);
        referenceModel1.setPartyId(PARTY_ID);
        referenceModel1.setId("id_13");
        referenceModel1.setIsGlobal(false);
        referenceDao.insert(referenceModel1);

        referenceModel1 = new ReferenceModel();
        referenceModel1.setTemplateId(GLOBAL_TEMPLATE);
        referenceModel1.setId("id_14");
        referenceModel1.setIsGlobal(true);
        referenceDao.insert(referenceModel1);

        referenceModel1 = new ReferenceModel();
        referenceModel1.setTemplateId(TEMPLATE_1);
        referenceModel1.setPartyId(PARTY_ID);
        referenceModel1.setShopId(SHOP_ID);
        referenceModel1.setId("id_15");
        referenceModel1.setIsGlobal(false);
        referenceDao.insert(referenceModel1);

        ResponseEntity<List<TemplateModel>> rulesByPartyAndShop = emulateResource.getRulesByPartyAndShop(PARTY_ID, SHOP_ID);

        Assert.assertTrue(rulesByPartyAndShop.hasBody());
        List<TemplateModel> templateModels = rulesByPartyAndShop.getBody();
        Assert.assertFalse(templateModels.isEmpty());
        Assert.assertEquals(5, templateModels.size());

        Assert.assertEquals(GLOBAL_TEMPLATE, templateModels.get(0).getId());
        Assert.assertEquals(TEMPLATE_2, templateModels.get(1).getId());
        Assert.assertEquals(TEMPLATE_1, templateModels.get(2).getId());
        Assert.assertEquals(TEMPLATE_3, templateModels.get(3).getId());
        Assert.assertEquals(TEMPLATE_1, templateModels.get(4).getId());

        referenceDao.remove(referenceModel1);

        rulesByPartyAndShop = emulateResource.getRulesByPartyAndShop(PARTY_ID, SHOP_ID);
        Assert.assertTrue(rulesByPartyAndShop.hasBody());
        templateModels = rulesByPartyAndShop.getBody();
        Assert.assertFalse(templateModels.isEmpty());
        Assert.assertEquals(4, templateModels.size());

        Assert.assertEquals(GLOBAL_TEMPLATE, templateModels.get(0).getId());
        Assert.assertEquals(TEMPLATE_2, templateModels.get(1).getId());
        Assert.assertEquals(TEMPLATE_1, templateModels.get(2).getId());
        Assert.assertEquals(TEMPLATE_3, templateModels.get(3).getId());
    }

    private void insertTemplate(String template, String id) {
        TemplateModel listRecord = new TemplateModel();
        listRecord.setTemplate(template);
        listRecord.setId(id);
        templateDao.insert(listRecord);
    }

}