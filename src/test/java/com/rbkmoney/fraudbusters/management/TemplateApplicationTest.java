package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.fraudbusters.management.dao.group.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.*;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.resource.payment.GroupCommandResource;
import com.rbkmoney.fraudbusters.management.resource.payment.TemplateCommandResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class TemplateApplicationTest extends AbstractKafkaIntegrationTest {

    @MockBean
    public TemplateDao templateDao;
    @MockBean
    public GroupDao groupDao;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public PaymentReferenceDao referenceDao;
    @MockBean
    public PaymentGroupReferenceDao groupReferenceDao;

    @Autowired
    TemplateCommandResource templateCommandResource;

    @Autowired
    GroupCommandResource groupCommandResource;

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
    public void groupTest() throws InterruptedException, IOException {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId("id");
        groupModel.setPriorityTemplates(List.of(new PriorityIdModel(1L, "test")));
        checkSerialization(groupModel);

        groupCommandResource.insertGroup(groupModel);
        groupCommandResource.removeGroup(groupModel);

        Thread.sleep(4000L);

        Mockito.verify(groupDao, Mockito.times(1)).insert(groupModel);
        Mockito.verify(groupDao, Mockito.times(1)).remove(groupModel);
    }

    private void checkSerialization(GroupModel groupModel) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(groupModel);
        GroupModel groupModelAfterSerialization = objectMapper.readValue(json, GroupModel.class);

        Assert.assertEquals(groupModel, groupModelAfterSerialization);
    }

    @Test
    public void referenceTest() throws InterruptedException {
        PaymentReferenceModel referenceModel = new PaymentReferenceModel();
        referenceModel.setId("id");
        referenceModel.setTemplateId("template_id");
        referenceModel.setIsGlobal(false);
        referenceModel.setPartyId("party_id");
        referenceModel.setShopId("shop_id");
        templateCommandResource.insertReference("id", Collections.singletonList(referenceModel));
        templateCommandResource.deleteReference("id", Collections.singletonList(referenceModel));
        Thread.sleep(200L);

        Mockito.verify(referenceDao, Mockito.times(1)).insert(any());
        Mockito.verify(referenceDao, Mockito.times(1)).remove((PaymentReferenceModel) any());
    }

    @Test
    public void groupReferenceTest() throws InterruptedException {
        PaymentGroupReferenceModel groupReferenceModel = new PaymentGroupReferenceModel();
        groupReferenceModel.setId("id");
        groupReferenceModel.setPartyId("party_id");
        groupReferenceModel.setShopId("shop_id");

        groupCommandResource.insertGroupReference("id", Collections.singletonList(groupReferenceModel));
        groupCommandResource.deleteGroupReference("id", Collections.singletonList(groupReferenceModel));
        Thread.sleep(4000L);

        Mockito.verify(groupReferenceDao, Mockito.times(1)).insert(any());
        Mockito.verify(groupReferenceDao, Mockito.times(1)).remove(any());
    }
}