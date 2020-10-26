package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.payment.group.GroupReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@ContextConfiguration(classes = {GroupReferenceDaoImpl.class})
public class GroupReferenceDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";
    public static final String GROUP_ID = "groupId";
    @Autowired
    PaymentGroupReferenceDao groupReferenceDao;

    @Test
    public void insert() {
        String id = "id";

        PaymentGroupReferenceModel referenceModel = new PaymentGroupReferenceModel();
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setShopId(SHOP_ID);
        referenceModel.setGroupId(GROUP_ID);
        groupReferenceDao.insert(referenceModel);

        List<PaymentGroupReferenceModel> byId = groupReferenceDao.getByGroupId(GROUP_ID);
        assertEquals(PARTY_ID, byId.get(0).getPartyId());

        groupReferenceDao.remove(referenceModel);

        byId = groupReferenceDao.getByGroupId(GROUP_ID);
        Assert.assertTrue(byId.isEmpty());

        groupReferenceDao.insert(referenceModel);
        List<PaymentGroupReferenceModel> paymentGroupReferenceModels = groupReferenceDao.filterReference(GROUP_ID, null, null, 1, null, null);
        assertEquals(PARTY_ID, paymentGroupReferenceModels.get(0).getPartyId());

        //check size
        referenceModel.setShopId(SHOP_ID + "2");
        groupReferenceDao.insert(referenceModel);
        paymentGroupReferenceModels = groupReferenceDao.filterReference(GROUP_ID, null, null, 1, null, null);
        assertEquals(1, paymentGroupReferenceModels.size());

        paymentGroupReferenceModels = groupReferenceDao.filterReference(GROUP_ID, null, null, 2, null, null);
        assertEquals(2, paymentGroupReferenceModels.size());

        //check pagination
        paymentGroupReferenceModels = groupReferenceDao.filterReference(GROUP_ID, null, null, 1, null, null);
        System.out.println(paymentGroupReferenceModels);

        List<PaymentGroupReferenceModel> secondPage = groupReferenceDao.filterReference(GROUP_ID,
                paymentGroupReferenceModels.get(0).getId(), GROUP_ID, 1, null, null);
        System.out.println(secondPage);
        assertNotEquals(paymentGroupReferenceModels.get(0).getShopId(), secondPage.get(0).getShopId());
    }
}
