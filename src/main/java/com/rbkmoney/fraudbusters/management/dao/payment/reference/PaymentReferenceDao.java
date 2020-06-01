package com.rbkmoney.fraudbusters.management.dao.payment.reference;

import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;

import java.util.List;

public interface PaymentReferenceDao extends ReferenceDao<PaymentReferenceModel> {

    List<PaymentReferenceModel> getListByTFilters(String partyId, String shopId, Boolean isGlobal, Boolean isDefault, int limit);

    List<PaymentReferenceModel> getByPartyAndShop(String partyId, String shopId);

    PaymentReferenceModel getDefaultReference();

}
