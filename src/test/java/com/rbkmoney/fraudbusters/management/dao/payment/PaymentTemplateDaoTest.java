package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@Slf4j
@ContextConfiguration(classes = {PaymentTemplateDao.class})
public class PaymentTemplateDaoTest {

    @Autowired
    TemplateDao templateDao;

    @Test
    void insertTest() {
        String id = "id";
        TemplateModel templateModel = createTemplateModel(id);
        templateDao.insert(templateModel);
        TemplateModel byId = templateDao.getById(id);
        assertEquals(templateModel.getId(), byId.getId());

        templateDao.remove(id);
        byId = templateDao.getById(id);
        assertNull(byId);
    }

    private TemplateModel createTemplateModel(String id) {
        TemplateModel templateModel = new TemplateModel();
        templateModel.setId(id);
        templateModel.setTemplate(
                "rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;" +
                        "rule:whiteList_1:inWhiteList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")" +
                        "->three_ds;" +
                        "rule:inCountry_1:in(countryBy(\"country_bank\")," +
                        "\"ARG\",\"AUS\",\"CAN\",\"CHL\",\"COL\",\"JPN\",\"MEX\",\"NZL\",\"PER\",\"GBR\",\"USA\")" +
                        "->decline;" +
                        "rule:amount_country30:amount()>3000 " +
                        "AND in(countryBy(\"country_bank\"),\"DEU\",\"ESP\",\"PHL\")->decline;" +
                        "rule:amount_country50:amount()>5000 " +
                        "AND in(countryBy(\"country_bank\"),\"BRA\",\"ISR\")->decline;rule:amount_5000:amount()>50000" +
                        "->decline;rule:count5:count(\"card_token\",1440)>5 " +
                        "AND NOT in(countryBy(\"country_bank\")," +
                        "\"BLR\",\"BRA\",\"EST\",\"GEO\",\"KAZ\",\"LTU\",\"LVA\",\"POL\",\"UKR\")->decline;" +
                        "rule:countForBLR10:count(\"card_token\",1440)>10->decline;" +
                        "rule:card_email_count_3:unique(\"card_token\",\"email\",1440)>3->decline;" +
                        "rule:check_unique:unique(\"card_token\",\"fingerprint\",1440)>3->decline;");

        return templateModel;
    }

    @Test
    void constraintDeduplicate() {
        String dedId = "ded_id";
        TemplateModel templateModel = createTemplateModel(dedId);
        templateDao.insert(templateModel);
        TemplateModel byId = templateDao.getById(dedId);
        assertEquals(templateModel.getId(), byId.getId());

        templateModel.setTemplate("rule:blackList_1:inBlackList");
        templateDao.insert(templateModel);
        byId = templateDao.getById(dedId);
        assertEquals(templateModel.getId(), byId.getId());

        templateDao.remove(dedId);
        byId = templateDao.getById(dedId);
        assertNull(byId);
    }

    @Test
    void filterTemplateTest() {
        TemplateModel templateModel = createTemplateModel("filter_id");
        templateDao.insert(templateModel);

        //filter with pagination
        templateModel = createTemplateModel("filter_id_2");
        templateDao.insert(templateModel);

        List<TemplateModel> list = templateDao.filterModel(new FilterRequest(
                null,
                null,
                null,
                1,
                null,
                SortOrder.DESC));
        log.info("list: {}", list);
        assertEquals(1, list.size());

        TemplateModel templateModel1 = list.get(0);
        list = templateDao.filterModel(new FilterRequest(
                null,
                templateModel1.getId(),
                null,
                1,
                null,
                SortOrder.DESC));
        log.info("list: {}", list);
        assertEquals(1, list.size());
        assertNotEquals(templateModel1.getId(), list.get(0).getId());

        //filter by id
        String filterIdRegexp = "filter_%";
        list = templateDao.filterModel(new FilterRequest(
                filterIdRegexp,
                null,
                null,
                2,
                null,
                SortOrder.DESC));
        log.info("list: {}", list);
        assertEquals(2, list.size());

        //filter and pagination by id
        list = templateDao.filterModel(new FilterRequest(
                filterIdRegexp,
                null,
                null,
                1,
                null,
                SortOrder.DESC));
        log.info("list: {}", list);
        assertEquals(1, list.size());

        String id = list.get(0).getId();
        list = templateDao.filterModel(new FilterRequest(
                null,
                id,
                null,
                1,
                null,
                SortOrder.DESC));
        log.info("list: {}", list);
        assertEquals(1, list.size());
        assertNotEquals(id, list.get(0).getId());

        Integer count = templateDao.countFilterModel(filterIdRegexp);
        assertEquals(Integer.valueOf(2), count);

        List<String> listNames = templateDao.getListNames("filter%");
        assertEquals(2, listNames.size());

        listNames = templateDao.getListNames("filter_id_%");
        assertEquals(1, listNames.size());
    }
}
