package com.rbkmoney.fraudbusters.management.kafka;

import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.management.config.KafkaITest;
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
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.filter.UnknownPaymentTemplateInReferenceFilter;
import com.rbkmoney.fraudbusters.management.resource.payment.PaymentGroupsResource;
import com.rbkmoney.fraudbusters.management.resource.payment.PaymentsReferenceResource;
import com.rbkmoney.fraudbusters.management.resource.payment.PaymentsTemplatesResource;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@KafkaITest
public class TemplateApplicationTest {

    public static final String PARTY_ID = "party_id";
    public static final String SHOP_ID = "shop_id";
    public static final String TEST = "test";
    public static final String ID = "id";
    public static final String TEMPLATE_ID = "template_id";

    @Value("${kafka.topic.fraudbusters.unknown-initiating-entity}")
    public String topicUnknownInitiatingEntity;

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
    PaymentsTemplatesResource paymentTemplateCommandResource;

    @Autowired
    PaymentsReferenceResource paymentsReferenceResource;

    @Autowired
    PaymentGroupsResource paymentGroupsResource;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @Test
    void templateTest() throws TException {
        TemplateModel templateModel = createTemplate(ID);
        paymentTemplateCommandResource.removeTemplate(ID);

        await().untilAsserted(() -> {
            verify(paymentTemplateDao, times(1)).insert(templateModel);
            verify(paymentTemplateDao, times(1)).remove(any(TemplateModel.class));
        });
    }

    private TemplateModel createTemplate(String id) throws TException {
        when(iface.validateCompilationTemplate(anyList())).thenReturn(new ValidateTemplateResponse()
                .setErrors(List.of()));

        TemplateModel templateModel = new TemplateModel();
        templateModel.setId(id);
        templateModel.setTemplate(
                "rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");
        paymentTemplateCommandResource.insertTemplate(new Template()
                .id(templateModel.getId())
                .template(templateModel.getTemplate())
        );
        return templateModel;
    }

    @Test
    void groupTest() throws IOException {
        Group groupModel = new Group();
        groupModel.setGroupId(ID);
        groupModel.setPriorityTemplates(List.of(new PriorityId()
                .id(TEST)
                .priority(1L)));
        checkSerialization(groupModel);

        paymentGroupsResource.insertGroup(groupModel);
        paymentGroupsResource.removeGroup(groupModel.getGroupId());

        await().untilAsserted(() -> {
            verify(paymentGroupDao, times(1)).insert(GroupModel.builder()
                    .groupId(groupModel.getGroupId())
                    .priorityTemplates(List.of(new PriorityIdModel(1L, "test", null))).build());
            verify(paymentGroupDao, times(1)).remove(any(GroupModel.class));
        });
    }

    private void checkSerialization(Group group) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(group);
        Group groupModelAfterSerialization = objectMapper.readValue(json, Group.class);

        assertEquals(group, groupModelAfterSerialization);
    }

    @Test
    void referenceTest() {
        when(unknownPaymentTemplateInReferenceFilter.test(any())).thenReturn(true);
        PaymentReference referenceModel = createPaymentReference(TEMPLATE_ID);

        final ResponseEntity<List<String>> references =
                paymentsReferenceResource.insertReferences(Collections.singletonList(referenceModel));

        when(referenceDao.getById(references.getBody().get(0))).thenReturn(createPaymentReferenceModel(TEMPLATE_ID));

        paymentsReferenceResource.removeReference(references.getBody().get(0));
        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
            verify(referenceDao, times(1)).remove((PaymentReferenceModel) any());
        });

        when(unknownPaymentTemplateInReferenceFilter.test(any())).thenReturn(false);
        Mockito.clearInvocations(referenceDao);
        referenceModel = createPaymentReference(TEMPLATE_ID);
        ResponseEntity<List<String>> listResponseEntity =
                paymentsReferenceResource.insertReferences(Collections.singletonList(referenceModel));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, listResponseEntity.getStatusCode());
        assertEquals(TEMPLATE_ID, listResponseEntity.getBody().get(0));
        verify(referenceDao, times(0)).insert(any());
    }

    private PaymentReference createPaymentReference(String templateId) {
        return new PaymentReference()
                .id(ID)
                .templateId(templateId)
                .partyId(PARTY_ID)
                .shopId(SHOP_ID);
    }

    private PaymentReferenceModel createPaymentReferenceModel(String templateId) {
        PaymentReferenceModel referenceModel = new PaymentReferenceModel();
        referenceModel.setId(ID);
        referenceModel.setTemplateId(templateId);
        referenceModel.setIsGlobal(false);
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setShopId(SHOP_ID);
        return referenceModel;
    }

    @Test
    void defaultReferenceTest() {
        when(defaultReferenceDao.getByPartyAndShop(any(), any())).thenReturn(Optional.of(buildDefaultReference()));

        ReferenceInfo referenceInfo = ReferenceInfo.merchant_info(new MerchantInfo()
                .setPartyId(PARTY_ID)
                .setShopId(SHOP_ID));
        testThriftKafkaProducer.send(topicUnknownInitiatingEntity, referenceInfo);

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
    void groupReferenceTest() {
        GroupReference groupReferenceModel = new GroupReference();
        groupReferenceModel.setId(ID);
        groupReferenceModel.setPartyId(PARTY_ID);
        groupReferenceModel.setShopId(SHOP_ID);

        paymentGroupsResource
                .insertGroupReferences(ID, Collections.singletonList(groupReferenceModel));
        paymentGroupsResource.removeGroupReference(null, ID, PARTY_ID, SHOP_ID);

        await().untilAsserted(() -> {
            verify(groupReferenceDao, times(1)).insert(any());
            verify(groupReferenceDao, times(1)).remove(any());
        });
    }
}
