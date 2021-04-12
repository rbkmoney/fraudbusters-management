package com.rbkmoney.fraudbusters.management.dao.payment.reference;

import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;

import java.util.List;

public interface PaymentReferenceDao extends ReferenceDao<PaymentReferenceModel> {

    List<PaymentReferenceModel> getListByTFilters(String partyId, String shopId, Integer limit);

    List<PaymentReferenceModel> filterReferences(FilterRequest filterRequest);

    List<PaymentReferenceModel> getByPartyAndShop(String partyId, String shopId);

    Integer countFilterModel(String searchValue);

    Boolean isPartyShopReferenceExist(String partyId, String shopId);

}
