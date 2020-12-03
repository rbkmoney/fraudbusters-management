package com.rbkmoney.fraudbusters.management.dao.payment;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.ReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import org.jetbrains.annotations.NotNull;
import org.jooq.SortOrder;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.rbkmoney.fraudbusters.management.domain.tables.FReference.F_REFERENCE;
import static org.junit.Assert.*;

@ContextConfiguration(classes = {ReferenceDaoImpl.class})
public class ReferenceDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String PARTY_ID = "party_id";
    public static final String TEMPLATE_ID = "template_id";
    public static final String SHOP_ID = "shop_id";
    public static final String SECOND = "second_";
    public static final String THIRD = "third_";

    @Autowired
    PaymentReferenceDao referenceDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void cleanup(){
        jdbcTemplate.execute("TRUNCATE " + F_REFERENCE.getSchema().getName() + "." + F_REFERENCE.getName());
    }

    @Test
    public void insert() {
        String id = "id";
        PaymentReferenceModel referenceModel = createReference(id);

        referenceDao.insert(referenceModel);

        ReferenceModel byId = referenceDao.getById(id);
        byId.setLastUpdateDate(null);
        assertEquals(referenceModel, byId);

        referenceDao.remove(referenceModel);

        byId = referenceDao.getById(id);
        assertNull(byId);
    }

    @NotNull
    private PaymentReferenceModel createReference(String id) {
        PaymentReferenceModel referenceModel = new PaymentReferenceModel();
        referenceModel.setId(id);
        referenceModel.setTemplateId(TEMPLATE_ID);
        referenceModel.setShopId(SHOP_ID);
        referenceModel.setPartyId(PARTY_ID);
        referenceModel.setIsGlobal(false);
        referenceModel.setIsDefault(false);
        return referenceModel;
    }

    @Test
    public void constraintTest() {
        String id = "id";
        PaymentReferenceModel referenceModel = createReference(id);

        referenceDao.insert(referenceModel);

        String test = "test";
        referenceModel.setTemplateId(test);
        referenceDao.insert(referenceModel);

        ReferenceModel byId = referenceDao.getById(id);
        assertEquals(byId.getTemplateId(), test);

        String firstGlobal = UUID.randomUUID().toString();
        referenceModel.setId(firstGlobal);
        referenceModel.setIsGlobal(true);
        referenceDao.insert(referenceModel);

        String global = "global";
        String globalId = UUID.randomUUID().toString();
        referenceModel.setId(globalId);
        referenceModel.setTemplateId(global);
        referenceModel.setIsGlobal(true);
        referenceDao.insert(referenceModel);

        byId = referenceDao.getById(globalId);
        assertEquals(byId.getTemplateId(), global);

        byId = referenceDao.getById(firstGlobal);
        assertNull(byId);

        List<PaymentReferenceModel> listByTFilters = referenceDao.getListByTFilters(PARTY_ID, null, null, null, 10);

        assertEquals(2, listByTFilters.size());

        listByTFilters = referenceDao.getListByTFilters(null, null, true, false, 10);

        assertEquals(1, listByTFilters.size());
    }

    @Test
    public void testDefault() throws IOException, InterruptedException {
        String id = "id";
        PaymentReferenceModel referenceModel = createReference(id);
        referenceDao.insert(referenceModel);
        referenceDao.markReferenceAsDefault(id);
        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        System.out.println(paymentReferenceModels);
        PaymentReferenceModel defaultReference = referenceDao.getDefaultReference();
        PaymentReferenceModel byId = referenceDao.getById(id);
        assertEquals(byId, defaultReference);
    }

    @Test
    public void filterTest() {
        String id = "filter_id";
        PaymentReferenceModel referenceModel = createReference(id);
        referenceDao.insert(referenceModel);

        referenceModel.setId(SECOND + id);
        referenceModel.setShopId(SECOND + SHOP_ID);
        referenceModel.setTemplateId(SECOND + TEMPLATE_ID);
        referenceModel.setIsDefault(true);
        referenceDao.insert(referenceModel);

        referenceModel.setId(THIRD + id);
        referenceModel.setShopId(THIRD + SHOP_ID);
        referenceModel.setTemplateId(THIRD + TEMPLATE_ID);
        referenceModel.setIsGlobal(true);
        referenceModel.setIsDefault(false);
        referenceDao.insert(referenceModel);

        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(3, paymentReferenceModels.size());

        //check template field
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(TEMPLATE_ID)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);

        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(1, paymentReferenceModels.size());

        //check regexp
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue("%" + TEMPLATE_ID + "%")
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(3, paymentReferenceModels.size());

        //check concrete
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(THIRD + TEMPLATE_ID)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(1, paymentReferenceModels.size());

        //check global
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), true, false);

        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(1, paymentReferenceModels.size());
        assertEquals(THIRD + id, paymentReferenceModels.get(0).getId());

        //check default
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, true);

        assertFalse(paymentReferenceModels.isEmpty());
        assertEquals(1, paymentReferenceModels.size());
        assertEquals(SECOND + id, paymentReferenceModels.get(0).getId());

        //check sort
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy("template_id")
                .sortOrder(SortOrder.ASC)
                .build(), false, false);
        assertEquals(SECOND + id, paymentReferenceModels.get(0).getId());

        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(5)
                .sortBy("template_id")
                .sortOrder(SortOrder.DESC)
                .build(), false, false);
        assertEquals(THIRD + id, paymentReferenceModels.get(0).getId());

        //check paging
        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(null)
                .sortFieldValue(null)
                .size(1)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        assertEquals(SECOND + id, paymentReferenceModels.get(0).getId());

        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(paymentReferenceModels.get(0).getId())
                .sortFieldValue(paymentReferenceModels.get(0).getTemplateId())
                .size(1)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        assertEquals(id, paymentReferenceModels.get(0).getId());

        paymentReferenceModels = referenceDao.filterReferences(FilterRequest.builder()
                .searchValue(null)
                .lastId(paymentReferenceModels.get(0).getId())
                .sortFieldValue(paymentReferenceModels.get(0).getTemplateId())
                .size(1)
                .sortBy(null)
                .sortOrder(null)
                .build(), false, false);
        assertEquals(THIRD + id, paymentReferenceModels.get(0).getId());

        paymentReferenceModels
                .forEach(paymentReferenceModel -> referenceDao.remove(paymentReferenceModel));
    }
}
