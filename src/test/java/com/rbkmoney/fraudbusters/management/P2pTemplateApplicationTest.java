package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.fraudbusters.P2PServiceSrv;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2PGroupDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.template.P2pTemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.filter.UnknownP2pTemplateInReferenceFilter;
import com.rbkmoney.fraudbusters.management.resource.p2p.P2PTemplateCommandResource;
import com.rbkmoney.fraudbusters.management.resource.p2p.P2pGroupCommandResource;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.thrift.TException;
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
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class P2pTemplateApplicationTest extends AbstractKafkaIntegrationTest {

    public static final String TEMPLATE_ID = "template_id";
    public static final String IDENTITY_ID = "identity_id";
    public static final String TEST = "test";
    public static final String ID = "id";

    @MockBean
    public P2pTemplateDao p2pTemplateDao;
    @MockBean
    public P2PGroupDao p2pGroupDao;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public P2pReferenceDao referenceDao;
    @MockBean
    public P2pGroupReferenceDao groupReferenceDao;
    @MockBean
    public P2PServiceSrv.Iface iface;
    @MockBean
    public AuditService auditService;
    @MockBean
    public UnknownP2pTemplateInReferenceFilter unknownP2pTemplateInReferenceFilter;

    @Autowired
    P2PTemplateCommandResource p2pTemplateCommandResource;

    @Autowired
    P2pGroupCommandResource groupCommandResource;

    @Test
    public void templateTest() throws TException {
        when(iface.validateCompilationTemplate(anyList())).thenReturn(new ValidateTemplateResponse()
                .setErrors(List.of()));

        TemplateModel templateModel = new TemplateModel();
        String id = ID;
        templateModel.setId(id);
        templateModel.setTemplate(
                "rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");

        p2pTemplateCommandResource.insertTemplate(new BasicUserPrincipal(TEST), templateModel);
        p2pTemplateCommandResource.removeTemplate(new BasicUserPrincipal(TEST), id);

        await().untilAsserted(() -> {
            verify(p2pTemplateDao, times(1)).insert(templateModel);
            verify(p2pTemplateDao, times(1)).remove(any(TemplateModel.class));
        });
    }

    @Test
    public void referenceTest() {
        when(unknownP2pTemplateInReferenceFilter.test(any())).thenReturn(true);

        P2pReferenceModel referenceModel = createReference(ID, TEMPLATE_ID);
        p2pTemplateCommandResource
                .insertReferences(new BasicUserPrincipal(TEST), ID, Collections.singletonList(referenceModel));
        p2pTemplateCommandResource.deleteReference(new BasicUserPrincipal(TEST), referenceModel.getTemplateId(),
                referenceModel.getIdentityId());

        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
            verify(referenceDao, times(1)).remove((P2pReferenceModel) any());
        });

        Mockito.clearInvocations(referenceDao);
        when(unknownP2pTemplateInReferenceFilter.test(any())).thenReturn(false);
        referenceModel = createReference(ID, TEMPLATE_ID);
        p2pTemplateCommandResource
                .insertReferences(new BasicUserPrincipal(TEST), ID, Collections.singletonList(referenceModel));
        verify(referenceDao, times(0)).insert(any());
    }

    public P2pReferenceModel createReference(String id, String templateId) {
        P2pReferenceModel referenceModel = new P2pReferenceModel();
        referenceModel.setId(id);
        referenceModel.setTemplateId(templateId);
        referenceModel.setIsGlobal(false);
        referenceModel.setIdentityId(IDENTITY_ID);
        return referenceModel;
    }

    @Test
    public void groupReferenceTest() {
        P2pGroupReferenceModel groupReferenceModel = new P2pGroupReferenceModel();
        groupReferenceModel.setId(ID);
        String identityId = IDENTITY_ID;
        groupReferenceModel.setIdentityId(identityId);

        groupCommandResource
                .insertGroupReference(new BasicUserPrincipal(TEST), ID, Collections.singletonList(groupReferenceModel));
        groupCommandResource.removeGroupReference(new BasicUserPrincipal(TEST), ID, identityId);

        await().untilAsserted(() -> {
            verify(groupReferenceDao, times(1)).insert(any());
            verify(groupReferenceDao, times(1)).remove((P2pGroupReferenceModel) any());
        });
    }
}
