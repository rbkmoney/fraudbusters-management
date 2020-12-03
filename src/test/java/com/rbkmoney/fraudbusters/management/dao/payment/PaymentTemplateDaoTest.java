package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@ContextConfiguration(classes = {PaymentTemplateDao.class})
public class PaymentTemplateDaoTest extends AbstractPostgresIntegrationTest {

    @Autowired
    TemplateDao templateDao;

    @Test
    public void insertTest() {
        String id = "id";
        TemplateModel templateModel = createTemplateModel(id);
        templateDao.insert(templateModel);
        TemplateModel byId = templateDao.getById(id);
        assertEquals(templateModel, byId);

        templateDao.remove(id);
        byId = templateDao.getById(id);
        assertNull(byId);
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
        assertEquals(templateModel, byId);

        templateModel.setTemplate("rule:blackList_1:inBlackList");
        templateDao.insert(templateModel);
        byId = templateDao.getById(ded_id);
        assertEquals(templateModel, byId);

        templateDao.remove(ded_id);
        byId = templateDao.getById(ded_id);
        assertNull(byId);
    }

    @Test
    public void getListTest() {
        TemplateModel templateModel = createTemplateModel("id");
        templateDao.insert(templateModel);
        List<TemplateModel> list = templateDao.getList(10);
        assertEquals(1, list.size());

        templateModel = createTemplateModel("id_2");
        templateDao.insert(templateModel);
        list = templateDao.getList(10);
        assertEquals(2, list.size());
    }

    @Test
    public void filterTemplateTest() {
        TemplateModel templateModel = createTemplateModel("filter_id");
        templateDao.insert(templateModel);

        //filter with pagination
        templateModel = createTemplateModel("filter_id_2");
        templateDao.insert(templateModel);

        List<TemplateModel> list = templateDao.filterModel(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .size(1)
                .sortOrder(SortOrder.DESC)
                .build());
        log.info("list: {}", list);
        assertEquals(1, list.size());

        TemplateModel templateModel1 = list.get(0);
        list = templateDao.filterModel(FilterRequest.builder()
                .searchValue(null)
                .lastId(templateModel1.getId())
                .size(1)
                .sortOrder(SortOrder.DESC)
                .build());
        log.info("list: {}", list);
        assertEquals(1, list.size());
        assertNotEquals(templateModel1.getId(), list.get(0).getId());

        //filter by id
        String filter_id_regexp = "filter_%";
        list = templateDao.filterModel(FilterRequest.builder()
                .searchValue(filter_id_regexp)
                .lastId(null)
                .size(2)
                .sortOrder(SortOrder.DESC)
                .build());
        log.info("list: {}", list);
        assertEquals(2, list.size());

        //filter and pagination by id
        list = templateDao.filterModel(FilterRequest.builder()
                .searchValue(filter_id_regexp)
                .lastId(null)
                .size(1)
                .sortOrder(SortOrder.DESC)
                .build());
        log.info("list: {}", list);
        assertEquals(1, list.size());

        String id = list.get(0).getId();
        list = templateDao.filterModel(FilterRequest.builder()
                .searchValue(null)
                .lastId(id)
                .size(1)
                .sortOrder(SortOrder.DESC)
                .build());
        log.info("list: {}", list);
        assertEquals(1, list.size());
        assertNotEquals(id, list.get(0).getId());

        Integer count = templateDao.countFilterModel(filter_id_regexp);
        assertEquals(Integer.valueOf(2), count);

        List<String> listNames = templateDao.getListNames("filter%");
        assertEquals(2, listNames.size());

        listNames = templateDao.getListNames("filter_id_%");
        assertEquals(1, listNames.size());
    }
}
