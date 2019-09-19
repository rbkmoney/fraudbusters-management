package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(classes = {GroupDaoImpl.class})
public class GroupDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String GROUP_1 = "group_1";
    public static final String TEST_TEMPL_2 = "test_templ_2";
    public static final String TEST_TEMPL_1 = "test_templ_1";

    @Autowired
    GroupDao groupDao;

    @Test
    public void insert() {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId(GROUP_1);
        groupModel.setPriorityTemplates(List.of(new Pair<>(2L, TEST_TEMPL_1), new Pair<>(1L, TEST_TEMPL_2)));
        groupDao.insert(groupModel);

        GroupModel byId = groupDao.getById(GROUP_1);

        Assert.assertEquals(2L, byId.getPriorityTemplates().size());

        groupDao.remove(groupModel);

        byId = groupDao.getById(GROUP_1);
        Assert.assertNull(byId.getGroupId());
    }
}