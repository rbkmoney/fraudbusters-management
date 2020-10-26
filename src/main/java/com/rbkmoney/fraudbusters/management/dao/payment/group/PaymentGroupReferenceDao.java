package com.rbkmoney.fraudbusters.management.dao.payment.group;

import com.rbkmoney.fraudbusters.management.dao.GroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import org.jooq.SortOrder;

import java.util.List;

public interface PaymentGroupReferenceDao extends GroupReferenceDao<PaymentGroupReferenceModel> {

    void remove(String partyId, String shopId);

    List<PaymentGroupReferenceModel> getByPartyIdAndShopId(String partyId, String shopId);

    List<PaymentGroupReferenceModel> filterReference(String filterValue, String lastId, String sortFieldValue,
                                                     Integer size, String sortingBy, SortOrder sortOrder);
}
