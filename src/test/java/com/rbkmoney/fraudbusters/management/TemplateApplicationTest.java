package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.resource.payment.GroupCommandResource;
import com.rbkmoney.fraudbusters.management.resource.payment.PaymentTemplateCommandResource;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class TemplateApplicationTest extends AbstractKafkaIntegrationTest {

    @MockBean
    public PaymentTemplateDao paymentTemplateDao;
    @MockBean
    public PaymentGroupDao paymentGroupDao;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public PaymentReferenceDao referenceDao;
    @MockBean
    public PaymentGroupReferenceDao groupReferenceDao;
    @MockBean
    public PaymentServiceSrv.Iface iface;
    @MockBean
    public AuditService auditService;

    @Autowired
    PaymentTemplateCommandResource paymentTemplateCommandResource;

    @Autowired
    GroupCommandResource groupCommandResource;

    @Test
    public void templateTest() throws InterruptedException, TException {
        when(iface.validateCompilationTemplate(anyList())).thenReturn(new ValidateTemplateResponse()
                .setErrors(List.of()));

        TemplateModel templateModel = new TemplateModel();
        String id = "id";
        templateModel.setId(id);
        templateModel.setTemplate("rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");
        paymentTemplateCommandResource.insertTemplate(new BasicUserPrincipal("test"), templateModel);
        paymentTemplateCommandResource.removeTemplate(new BasicUserPrincipal("test"), id);

        await().untilAsserted(() -> {
            verify(paymentTemplateDao, times(1)).insert(templateModel);
            verify(paymentTemplateDao, times(1)).remove(any(TemplateModel.class));
        });
    }

    @Test
    public void groupTest() throws InterruptedException, IOException {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId("id");
        groupModel.setPriorityTemplates(List.of(new PriorityIdModel(1L, "test", null)));
        checkSerialization(groupModel);

        groupCommandResource.insertGroup(new BasicUserPrincipal("test"), groupModel);
        groupCommandResource.removeGroup(new BasicUserPrincipal("test"), groupModel.getGroupId());

        await().untilAsserted(() -> {
            verify(paymentGroupDao, times(1)).insert(groupModel);
            verify(paymentGroupDao, times(1)).remove(any(GroupModel.class));
        });
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

        final ResponseEntity<List<String>> references = paymentTemplateCommandResource.insertReferences(new BasicUserPrincipal("test"),
                Collections.singletonList(referenceModel));

        when(referenceDao.getById(references.getBody().get(0))).thenReturn(referenceModel);
        paymentTemplateCommandResource.removeReference(new BasicUserPrincipal("test"), references.getBody().get(0));
        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
            verify(referenceDao, times(1)).remove((PaymentReferenceModel) any());
        });
    }

    private PaymentReferenceModel buildDefaultReference() {
        PaymentReferenceModel paymentReferenceModel = new PaymentReferenceModel();
        paymentReferenceModel.setTemplateId("default_template_id");
        paymentReferenceModel.setIsGlobal(false);
        paymentReferenceModel.setIsDefault(true);
        return paymentReferenceModel;
    }

    @Test
    public void defaultReferenceTest() throws InterruptedException {

        when(referenceDao.getDefaultReference()).thenReturn(buildDefaultReference());

        PaymentReferenceModel referenceModel = new PaymentReferenceModel();
        referenceModel.setId("id");
        referenceModel.setIsGlobal(false);
        referenceModel.setPartyId("party_id");
        referenceModel.setShopId("shop_id");
        try (Producer<String, ReferenceInfo> producer = createProducer()) {
            ProducerRecord<String, ReferenceInfo> producerRecord = new ProducerRecord<>(UNKNOWN_INITIATING_ENTITY, "test",
                    ReferenceInfo.merchant_info(new MerchantInfo()
                    .setPartyId("party_id")
                    .setShopId("shop_id")));
            producer.send(producerRecord).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
        });
    }

    @Test
    public void groupReferenceTest() throws InterruptedException {
        PaymentGroupReferenceModel groupReferenceModel = new PaymentGroupReferenceModel();
        groupReferenceModel.setId("id");
        groupReferenceModel.setPartyId("party_id");
        groupReferenceModel.setShopId("shop_id");

        groupCommandResource.insertGroupReference(new BasicUserPrincipal("test"),
                "id",
                Collections.singletonList(groupReferenceModel));
        groupCommandResource.removeGroupReference(new BasicUserPrincipal("test"),
                "id",
                "party_id",
                "shop_id");

        await().untilAsserted(() -> {
            verify(groupReferenceDao, times(1)).insert(any());
            verify(groupReferenceDao, times(1)).remove(any());
        });
    }
}
