package com.rbkmoney.fraudbusters.management.dao.p2p.group;

import com.rbkmoney.fraudbusters.management.dao.GroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;

import java.util.List;

public interface P2pGroupReferenceDao extends GroupReferenceDao<P2pGroupReferenceModel> {

    void remove(String identityId);

    //todo не используется, выпиливаем?
    List<P2pGroupReferenceModel> getByIdentityId(String identityId);

    List<P2pGroupReferenceModel> filterReference(FilterRequest filterRequest);

    Integer countFilterReference(String filterValue);
}
