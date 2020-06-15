package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.damsel.fraudbusters.P2PValidateServiceSrv;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.management.dao.group.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.resource.p2p.P2PTemplateCommandResource;
import com.rbkmoney.fraudbusters.management.resource.p2p.P2pGroupCommandResource;
import lombok.extern.slf4j.Slf4j;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
@SpringBootTest(classes = FraudbustersManagementApplication.class)
public class P2pTemplateApplicationTest extends AbstractKafkaIntegrationTest {

    @MockBean
    public TemplateDao templateDao;
    @MockBean
    public GroupDao groupDao;
    @MockBean
    public WbListDao wbListDao;
    @MockBean
    public P2pReferenceDao referenceDao;
    @MockBean
    public P2pGroupReferenceDao groupReferenceDao;
    @MockBean
    public P2PValidateServiceSrv.Iface iface;

    @Autowired
    P2PTemplateCommandResource p2pTemplateCommandResource;

    @Autowired
    P2pGroupCommandResource groupCommandResource;

    @Test
    public void templateTest() throws InterruptedException, TException {
        when(iface.validateCompilationTemplate(anyList())).thenReturn(new ValidateTemplateResponse()
                .setErrors(List.of()));

        TemplateModel templateModel = new TemplateModel();
        templateModel.setId("id");
        templateModel.setTemplate("rule:blackList_1:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->decline;");
        p2pTemplateCommandResource.insertTemplate(templateModel);
        p2pTemplateCommandResource.removeTemplate(templateModel);
        Thread.sleep(4000L);

        verify(templateDao, times(1)).insert(templateModel);
        verify(templateDao, times(1)).remove(templateModel);
    }

    @Test
    public void referenceTest() throws InterruptedException {
        P2pReferenceModel referenceModel = new P2pReferenceModel();
        referenceModel.setId("id");
        referenceModel.setTemplateId("template_id");
        referenceModel.setIsGlobal(false);
        referenceModel.setIdentityId("identity_id");
        p2pTemplateCommandResource.insertReference("id", Collections.singletonList(referenceModel));
        p2pTemplateCommandResource.deleteReference("id", Collections.singletonList(referenceModel));
        Thread.sleep(200L);

        verify(referenceDao, times(1)).insert(any());
        verify(referenceDao, times(1)).remove((P2pReferenceModel) any());
    }

    @Test
    public void groupReferenceTest() throws InterruptedException {
        P2pGroupReferenceModel groupReferenceModel = new P2pGroupReferenceModel();
        groupReferenceModel.setId("id");
        groupReferenceModel.setIdentityId("identity_id");

        groupCommandResource.insertGroupReference("id", Collections.singletonList(groupReferenceModel));
        groupCommandResource.deleteGroupReference("id", Collections.singletonList(groupReferenceModel));
        Thread.sleep(4000L);

        verify(groupReferenceDao, times(1)).insert(any());
        verify(groupReferenceDao, times(1)).remove((P2pGroupReferenceModel) any());
    }
}