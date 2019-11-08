package com.rbkmoney.fraudbusters.management.dao.payment.template;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(classes = {TemplateDaoImpl.class})
public class TemplateDaoImplTest extends AbstractPostgresIntegrationTest {

    @Autowired
    TemplateDao templateDao;

    @Test
    public void insertTest() {
        String id = "id";
        TemplateModel templateModel = createTemplateModel(id);
        templateDao.insert(templateModel);
        TemplateModel byId = templateDao.getById(id);
        Assert.assertEquals(templateModel, byId);

        templateDao.remove(id);
        byId = templateDao.getById(id);
        Assert.assertNull(byId);
    }

    @NotNull
    private TemplateModel createTemplateModel(String id) {
        TemplateModel templateModel = new TemplateModel();
        templateModel.setId(id);
        templateModel.setTemplate("rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;rule:whiteList_1:inWhiteList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->three_ds;rule:inCountry_1:in(countryBy(\"country_bank\"),\"ARG\",\"AUS\",\"CAN\",\"CHL\",\"COL\",\"JPN\",\"MEX\",\"NZL\",\"PER\",\"GBR\",\"USA\")->decline;rule:amount_country30:amount()>3000 AND in(countryBy(\"country_bank\"),\"DEU\",\"ESP\",\"PHL\")->decline;rule:amount_country50:amount()>5000 AND in(countryBy(\"country_bank\"),\"BRA\",\"ISR\")->decline;rule:amount_5000:amount()>50000->decline;rule:count5:count(\"card_token\",1440)>5 AND NOT in(countryBy(\"country_bank\"),\"BLR\",\"BRA\",\"EST\",\"GEO\",\"KAZ\",\"LTU\",\"LVA\",\"POL\",\"UKR\")->decline;rule:countForBLR10:count(\"card_token\",1440)>10->decline;rule:card_email_count_3:unique(\"card_token\",\"email\",1440)>3->decline;rule:check_unique:unique(\"card_token\",\"fingerprint\",1440)>3->decline;");
        return templateModel;
    }

    @Test
    public void constraintDeduplicate() {
        String ded_id = "ded_id";
        TemplateModel templateModel = createTemplateModel(ded_id);
        templateDao.insert(templateModel);
        TemplateModel byId = templateDao.getById(ded_id);
        Assert.assertEquals(templateModel, byId);

        templateModel.setTemplate("rule:blackList_1:inBlackList");
        templateDao.insert(templateModel);
        byId = templateDao.getById(ded_id);
        Assert.assertEquals(templateModel, byId);

        templateDao.remove(ded_id);
        byId = templateDao.getById(ded_id);
        Assert.assertNull(byId);
    }

    @Test
    public void getListTest() {
        TemplateModel templateModel = createTemplateModel("id");
        templateDao.insert(templateModel);
        List<TemplateModel> list = templateDao.getList(10);
        Assert.assertEquals(1, list.size());

        templateModel = createTemplateModel("id_2");
        templateDao.insert(templateModel);
        list = templateDao.getList(10);
        Assert.assertEquals(2, list.size());
    }
}