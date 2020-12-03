package com.rbkmoney.fraudbusters.management.dao.p2p.reference;

import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import org.jooq.SortOrder;

import java.util.List;

public interface P2pReferenceDao extends ReferenceDao<P2pReferenceModel> {

    List<P2pReferenceModel> getListByTFilters(String identityId, Boolean isGlobal, Integer limit);

    //todo не используется, выпиливаем?
    List<P2pReferenceModel> getByIdentity(String identityId);

    List<P2pReferenceModel> filterReferences(FilterRequest filterRequest, boolean isGlobal);

    Integer countFilterModel(String searchValue, Boolean isGlobal);

}
