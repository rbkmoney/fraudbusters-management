package com.rbkmoney.fraudbusters.management.dao.p2p.reference;

import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;

import java.util.List;

public interface P2pReferenceDao extends ReferenceDao<P2pReferenceModel> {

    List<P2pReferenceModel> getListByTFilters(String identityId, Boolean isGlobal, Integer limit);

    List<P2pReferenceModel> getByIdentity(String identityId);

}
