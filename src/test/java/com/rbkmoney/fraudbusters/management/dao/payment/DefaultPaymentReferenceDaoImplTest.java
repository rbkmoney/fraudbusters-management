package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

@ContextConfiguration(classes = {DefaultPaymentReferenceDaoImpl.class})
public class DefaultPaymentReferenceDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String PARTY_ID = "party_id";
    public static final String TEST = "test";

    @Autowired
    DefaultPaymentReferenceDaoImpl referenceDao;

    @Test
    public void insert() {
        final String uid = UUID.randomUUID().toString();
        DefaultPaymentReferenceModel referenceModel = createReference(uid);
        referenceDao.insert(referenceModel);

        DefaultPaymentReferenceModel byId = referenceDao.getById(uid);
        DefaultPaymentReferenceModel byPartyAndShop = referenceDao.getByPartyAndShop(PARTY_ID, null);
        Assert.assertEquals(PARTY_ID, byPartyAndShop.getPartyId());
        Assert.assertEquals(byId.getPartyId(), byPartyAndShop.getPartyId());

        byPartyAndShop = referenceDao.getByPartyAndShop(null, null);
        Assert.assertNull(byPartyAndShop);

        referenceDao.remove(referenceModel);
        Assert.assertNull(referenceDao.getById(uid));
    }

    private DefaultPaymentReferenceModel createReference(String uid) {
        DefaultPaymentReferenceModel referenceModel = new DefaultPaymentReferenceModel();
        referenceModel.setId(uid);
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setTemplateId(TEST);
        return referenceModel;
    }
}
