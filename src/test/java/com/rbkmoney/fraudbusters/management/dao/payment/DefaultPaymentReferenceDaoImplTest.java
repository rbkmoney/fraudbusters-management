package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {DefaultPaymentReferenceDaoImpl.class})
public class DefaultPaymentReferenceDaoImplTest {

    public static final String PARTY_ID = "party_id";
    public static final String TEST = "test";
    public static final String SHOP_ID = "shop_id";

    @Autowired
    DefaultPaymentReferenceDaoImpl referenceDao;

    @Test
    void insert() {
        final String uid = UUID.randomUUID().toString();
        DefaultPaymentReferenceModel referenceModel = createReference(uid);
        referenceDao.insert(referenceModel);

        DefaultPaymentReferenceModel byId = referenceDao.getById(uid);
        DefaultPaymentReferenceModel byPartyAndShop = referenceDao.getByPartyAndShop(PARTY_ID, null).get();
        assertEquals(PARTY_ID, byPartyAndShop.getPartyId());
        assertEquals(byId.getPartyId(), byPartyAndShop.getPartyId());

        final Optional<DefaultPaymentReferenceModel> partyAndShop = referenceDao.getByPartyAndShop(null, null);
        assertTrue(partyAndShop.isEmpty());

        referenceDao.remove(uid);
        assertNull(referenceDao.getById(uid));
    }

    @Test
    void filter() {
        final String uid_1 = UUID.randomUUID().toString();
        DefaultPaymentReferenceModel referenceModel = createReference(uid_1);
        referenceDao.insert(referenceModel);

        final FilterRequest filterRequest = new FilterRequest();
        filterRequest.setSearchValue("%");
        filterRequest.setSize(10);
        List<DefaultPaymentReferenceModel> defaultPaymentReferenceModels = referenceDao.filterReferences(filterRequest);
        assertEquals(PARTY_ID, defaultPaymentReferenceModels.get(0).getPartyId());

        final String uid_2 = UUID.randomUUID().toString();
        referenceModel = createReference(uid_2);
        referenceModel.setShopId(SHOP_ID);
        referenceDao.insert(referenceModel);
        filterRequest.setSearchValue(SHOP_ID);
        defaultPaymentReferenceModels = referenceDao.filterReferences(filterRequest);
        assertEquals(SHOP_ID, defaultPaymentReferenceModels.get(0).getShopId());

        filterRequest.setSearchValue("");
        defaultPaymentReferenceModels = referenceDao.filterReferences(filterRequest);
        assertEquals(2, defaultPaymentReferenceModels.size());

        filterRequest.setSearchValue(null);
        defaultPaymentReferenceModels = referenceDao.filterReferences(filterRequest);
        assertEquals(2, defaultPaymentReferenceModels.size());

        filterRequest.setSearchValue("test_xxx");
        defaultPaymentReferenceModels = referenceDao.filterReferences(filterRequest);
        assertTrue(CollectionUtils.isEmpty(defaultPaymentReferenceModels));

        referenceDao.remove(uid_1);
        referenceDao.remove(uid_2);
    }

    private DefaultPaymentReferenceModel createReference(String uid) {
        DefaultPaymentReferenceModel referenceModel = new DefaultPaymentReferenceModel();
        referenceModel.setId(uid);
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setTemplateId(TEST);
        return referenceModel;
    }
}
