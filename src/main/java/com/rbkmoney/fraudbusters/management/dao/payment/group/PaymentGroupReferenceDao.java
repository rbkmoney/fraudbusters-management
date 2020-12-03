package com.rbkmoney.fraudbusters.management.dao.payment.group;

import com.rbkmoney.fraudbusters.management.dao.GroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import org.jooq.SortOrder;

import java.util.List;

public interface PaymentGroupReferenceDao extends GroupReferenceDao<PaymentGroupReferenceModel> {

    void remove(String partyId, String shopId);

    List<PaymentGroupReferenceModel> getByPartyIdAndShopId(String partyId, String shopId);

    List<PaymentGroupReferenceModel> filterReference(FilterRequest filterRequest);

    Integer countFilterReference(String filterValue);
}
