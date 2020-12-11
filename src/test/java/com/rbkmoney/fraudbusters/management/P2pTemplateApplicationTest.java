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
import com.rbkmoney.fraudbusters.management.resource.p2p.P2PTemplateCommandResource;
import com.rbkmoney.fraudbusters.management.resource.p2p.P2pGroupCommandResource;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Autowired
    P2PTemplateCommandResource p2pTemplateCommandResource;

    @Autowired
    P2pGroupCommandResource groupCommandResource;

    @Test
    public void templateTest() throws InterruptedException, TException {
        when(iface.validateCompilationTemplate(anyList())).thenReturn(new ValidateTemplateResponse()
                .setErrors(List.of()));

        TemplateModel templateModel = new TemplateModel();
        String id = "id";
        templateModel.setId(id);
        templateModel.setTemplate("rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");

        p2pTemplateCommandResource.insertTemplate(new BasicUserPrincipal("test"), templateModel);
        p2pTemplateCommandResource.removeTemplate(new BasicUserPrincipal("test"), id);

        await().untilAsserted(() -> {
            verify(p2pTemplateDao, times(1)).insert(templateModel);
            verify(p2pTemplateDao, times(1)).remove(any(TemplateModel.class));
        });
    }

    @Test
    public void referenceTest() throws InterruptedException {
        P2pReferenceModel referenceModel = new P2pReferenceModel();
        referenceModel.setId("id");
        referenceModel.setTemplateId("template_id");
        referenceModel.setIsGlobal(false);
        referenceModel.setIdentityId("identity_id");
        p2pTemplateCommandResource.insertReferences(new BasicUserPrincipal("test"), "id", Collections.singletonList(referenceModel));
        p2pTemplateCommandResource.deleteReference(new BasicUserPrincipal("test"), referenceModel.getTemplateId(), referenceModel.getIdentityId());

        await().untilAsserted(() -> {
            verify(referenceDao, times(1)).insert(any());
            verify(referenceDao, times(1)).remove((P2pReferenceModel) any());
        });
    }

    @Test
    public void groupReferenceTest() throws InterruptedException {
        P2pGroupReferenceModel groupReferenceModel = new P2pGroupReferenceModel();
        String id = "id";
        groupReferenceModel.setId(id);
        String identity_id = "identity_id";
        groupReferenceModel.setIdentityId(identity_id);

        groupCommandResource.insertGroupReference(new BasicUserPrincipal("test"), id, Collections.singletonList(groupReferenceModel));
        groupCommandResource.removeGroupReference(new BasicUserPrincipal("test"), id, identity_id);

        await().untilAsserted(() -> {
            verify(groupReferenceDao, times(1)).insert(any());
            verify(groupReferenceDao, times(1)).remove((P2pGroupReferenceModel) any());
        });
    }
}
