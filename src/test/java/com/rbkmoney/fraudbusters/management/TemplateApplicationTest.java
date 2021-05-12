package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.filter.UnknownPaymentTemplateInReferenceFilter;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class TemplateApplicationTest extends AbstractKafkaIntegrationTest {

    public static final String PARTY_ID = "party_id";
    public static final String SHOP_ID = "shop_id";
    public static final String TEST = "test";
    public static final String ID = "id";
    public static final String TEMPLATE_ID = "template_id";

    @MockBean
    public PaymentTemplateDao paymentTemplateDao;
    @MockBean
    public PaymentGroupDao paymentGroupDao;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public PaymentReferenceDaoImpl referenceDao;
    @MockBean
    public DefaultPaymentReferenceDaoImpl defaultReferenceDao;
    @MockBean
    public PaymentGroupReferenceDao groupReferenceDao;
    @MockBean
    public PaymentServiceSrv.Iface iface;
    @MockBean
    public AuditService auditService;
    @MockBean
    public UnknownPaymentTemplateInReferenceFilter unknownPaymentTemplateInReferenceFilter;

    @Autowired
    PaymentTemplateCommandResource paymentTemplateCommandResource;

    @Autowired
    GroupCommandResource groupCommandResource;

    @Test
    public void templateTest() throws TException {
        TemplateModel templateModel = createTemplate(ID);
        paymentTemplateCommandResource.removeTemplate(new BasicUserPrincipal(TEST), ID);

        await().untilAsserted(() -> {
            verify(paymentTemplateDao, times(1)).insert(templateModel);
            verify(paymentTemplateDao, times(1)).remove(any(TemplateModel.class));
        });
    }

    public TemplateModel createTemplate(String id) throws TException {
        when(iface.validateCompilationTemplate(anyList())).thenReturn(new ValidateTemplateResponse()
                .setErrors(List.of()));

        TemplateModel templateModel = new TemplateModel();
        templateModel.setId(id);
        templateModel.setTemplate(
                "rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");
        paymentTemplateCommandResource.insertTemplate(new BasicUserPrincipal(TEST), templateModel);
        return templateModel;
    }

    @Test
    public void groupTest() throws IOException {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId(ID);
        groupModel.setPriorityTemplates(List.of(new PriorityIdModel(1L, TEST, null)));
        checkSerialization(groupModel);

        groupCommandResource.insertGroup(new BasicUserPrincipal(TEST), groupModel);
        groupCommandResource.removeGroup(new BasicUserPrincipal(TEST), groupModel.getGroupId());

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
    public void referenceTest() {
        when(unknownPaymentTemplateInReferenceFilter.test(any())).thenReturn(true);
        PaymentReferenceModel referenceModel = createPaymentReferenceModel(TEMPLATE_ID);

        final ResponseEntity<List<String>> references =
                paymentTemplateCommandResource.insertReferences(new BasicUserPrincipal(TEST),
                        Collections.singletonList(referenceModel));

        when(referenceDao.getById(references.getBody().get(0))).thenReturn(referenceModel);
        paymentTemplateCommandResource.removeReference(new BasicUserPrincipal(TEST), references.getBody().get(0));
        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
            verify(referenceDao, times(1)).remove((PaymentReferenceModel) any());
        });

        when(unknownPaymentTemplateInReferenceFilter.test(any())).thenReturn(false);
        Mockito.clearInvocations(referenceDao);
        referenceModel = createPaymentReferenceModel(TEMPLATE_ID);
        ResponseEntity<List<String>> listResponseEntity =
                paymentTemplateCommandResource.insertReferences(new BasicUserPrincipal(TEST),
                        Collections.singletonList(referenceModel));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, listResponseEntity.getStatusCode());
        assertEquals(TEMPLATE_ID, listResponseEntity.getBody().get(0));
        verify(referenceDao, times(0)).insert(any());
    }

    public PaymentReferenceModel createPaymentReferenceModel(String templateId) {
        PaymentReferenceModel referenceModel = new PaymentReferenceModel();
        referenceModel.setId(ID);
        referenceModel.setTemplateId(templateId);
        referenceModel.setIsGlobal(false);
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setShopId(SHOP_ID);
        return referenceModel;
    }

    @Test
    public void defaultReferenceTest() {
        when(defaultReferenceDao.getByPartyAndShop(any(), any())).thenReturn(Optional.of(buildDefaultReference()));

        try (Producer<String, ReferenceInfo> producer = createProducer()) {
            ProducerRecord<String, ReferenceInfo> producerRecord = new ProducerRecord<>(UNKNOWN_INITIATING_ENTITY, TEST,
                    ReferenceInfo.merchant_info(new MerchantInfo()
                            .setPartyId(PARTY_ID)
                            .setShopId(SHOP_ID))
            );
            producer.send(producerRecord).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
        });
    }

    private DefaultPaymentReferenceModel buildDefaultReference() {
        DefaultPaymentReferenceModel paymentReferenceModel = new DefaultPaymentReferenceModel();
        paymentReferenceModel.setTemplateId("default_template_id");
        return paymentReferenceModel;
    }

    @Test
    public void groupReferenceTest() {
        PaymentGroupReferenceModel groupReferenceModel = new PaymentGroupReferenceModel();
        groupReferenceModel.setId(ID);
        groupReferenceModel.setPartyId(PARTY_ID);
        groupReferenceModel.setShopId(SHOP_ID);

        groupCommandResource
                .insertGroupReference(new BasicUserPrincipal(TEST), ID, Collections.singletonList(groupReferenceModel));
        groupCommandResource.removeGroupReference(new BasicUserPrincipal(TEST), ID, PARTY_ID, SHOP_ID);

        await().untilAsserted(() -> {
            verify(groupReferenceDao, times(1)).insert(any());
            verify(groupReferenceDao, times(1)).remove(any());
        });
    }
}
