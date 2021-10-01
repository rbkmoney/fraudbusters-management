package com.rbkmoney.fraudbusters.management.dao.p2p;

import com.rbkmoney.fraudbusters.management.config.PostgresqlJooqITest;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pGroupReferenceModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PostgresqlJooqITest
@ContextConfiguration(classes = {P2pGroupReferenceDaoImpl.class})
public class P2pGroupReferenceDaoImplTest {

    public static final String GROUP_ID = "groupId";
    public static final String IDENTITY_ID = "identity_id";

    @Autowired
    P2pGroupReferenceDao groupReferenceDao;

    @Test
    public void insert() {
        P2pGroupReferenceModel referenceModel = new P2pGroupReferenceModel();
        referenceModel.setIdentityId(IDENTITY_ID);
        referenceModel.setGroupId(GROUP_ID);
        groupReferenceDao.insert(referenceModel);

        List<P2pGroupReferenceModel> byId = groupReferenceDao.getByGroupId(GROUP_ID);
        assertEquals(IDENTITY_ID, byId.get(0).getIdentityId());

        groupReferenceDao.remove(referenceModel);

        byId = groupReferenceDao.getByGroupId(GROUP_ID);
        assertTrue(byId.isEmpty());
    }
}