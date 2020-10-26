package com.rbkmoney.fraudbusters.management.dao.p2p.group;

import com.rbkmoney.fraudbusters.management.dao.GroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import org.jooq.SortOrder;

import java.util.List;

public interface P2pGroupReferenceDao extends GroupReferenceDao<P2pGroupReferenceModel> {

    void remove(String identityId);

    List<P2pGroupReferenceModel> getByIdentityId(String identityId);

    List<P2pGroupReferenceModel> filterReference(String filterValue, String lastId, String sortFieldValue,
                                                     Integer size, String sortingBy, SortOrder sortOrder);

    Integer countFilterReference(String filterValue);
}
