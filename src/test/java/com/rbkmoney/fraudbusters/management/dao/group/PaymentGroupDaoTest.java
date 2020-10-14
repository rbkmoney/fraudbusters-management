package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.utils.GroupRowToModelMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(classes = {PaymentGroupDao.class, GroupRowToModelMapper.class})
public class PaymentGroupDaoTest extends AbstractPostgresIntegrationTest {

    public static final String GROUP_1 = "group_1";
    public static final String GROUP_2 = "group_2";
    public static final String TEST_TEMPL_2 = "test_templ_2";
    public static final String TEST_TEMPL_1 = "test_templ_1";

    @Autowired
    GroupDao groupDao;

    @Test
    public void insert() {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId(GROUP_1);
        groupModel.setPriorityTemplates(List.of(
                new PriorityIdModel(2L, TEST_TEMPL_1),
                new PriorityIdModel(1L, TEST_TEMPL_2)));
        groupDao.insert(groupModel);

        GroupModel byId = groupDao.getById(GROUP_1);

        Assert.assertEquals(2L, byId.getPriorityTemplates().size());

        groupModel.setGroupId(GROUP_2);
        groupDao.insert(groupModel);

        List<GroupModel> groupModels = groupDao.filterGroup(null);
        Assert.assertEquals(2, groupModels.size());

        groupModels = groupDao.filterGroup("group%");
        Assert.assertEquals(2, groupModels.size());

        groupModels = groupDao.filterGroup("test_templ_2");
        Assert.assertEquals(2, groupModels.size());

        groupDao.remove(groupModel);
        byId = groupDao.getById(GROUP_2);
        Assert.assertNull(byId.getGroupId());
    }
}
