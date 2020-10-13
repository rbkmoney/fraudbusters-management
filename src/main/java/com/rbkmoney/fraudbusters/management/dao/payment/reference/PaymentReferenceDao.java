package com.rbkmoney.fraudbusters.management.dao.payment.reference;

import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import org.jooq.SortOrder;

import java.util.List;

public interface PaymentReferenceDao extends ReferenceDao<PaymentReferenceModel> {

    List<PaymentReferenceModel> getListByTFilters(String partyId, String shopId, Boolean isGlobal, Boolean isDefault, Integer limit);

    List<PaymentReferenceModel> filterReferences(String searchValue, Boolean isGlobal, Boolean isDefault,
                                                 String lastId, Integer size, String sortingBy, SortOrder sortOrder);

    List<PaymentReferenceModel> getByPartyAndShop(String partyId, String shopId);

    PaymentReferenceModel getDefaultReference();

    void markReferenceAsDefault(String id);

    Integer countFilterModel(String searchValue, Boolean isGlobal, Boolean isDefault);

}
