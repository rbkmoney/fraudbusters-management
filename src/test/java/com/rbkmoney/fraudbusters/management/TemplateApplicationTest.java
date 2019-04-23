package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.fraudbusters.management.dao.reference.ReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.resource.TemplateCommandResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class TemplateApplicationTest extends AbstractKafkaIntegrationTest {

    @MockBean
    public TemplateDao templateDao;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public ReferenceDao referenceDao;

    @Autowired
    TemplateCommandResource templateCommandResource;

    @Test
    public void templateTest() throws InterruptedException {
        TemplateModel templateModel = new TemplateModel();
        templateModel.setId("id");
        templateModel.setTemplate("rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");
        templateCommandResource.insertTemplate(templateModel);
        templateCommandResource.removeTemplate(templateModel);
        Thread.sleep(4000L);

        Mockito.verify(templateDao, Mockito.times(1)).insert(templateModel);
        Mockito.verify(templateDao, Mockito.times(1)).remove(templateModel);
    }

    @Test
    public void referenceTest() throws InterruptedException {
        ReferenceModel referenceModel = new ReferenceModel();
        referenceModel.setId("id");
        referenceModel.setTemplateId("template_id");
        referenceModel.setIsGlobal(false);
        referenceModel.setPartyId("party_id");
        referenceModel.setShopId("shop_id");
        templateCommandResource.insertReference("id", Collections.singletonList(referenceModel));
        templateCommandResource.deleteReference("id", Collections.singletonList(referenceModel));
        Thread.sleep(200L);

        Mockito.verify(referenceDao, Mockito.times(1)).insert(any());
        Mockito.verify(referenceDao, Mockito.times(1)).remove((ReferenceModel) any());
    }
}