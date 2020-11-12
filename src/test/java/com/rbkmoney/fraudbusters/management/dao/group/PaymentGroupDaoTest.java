package com.rbkmoney.fraudbusters.management.dao.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.utils.GroupRowToModelMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {PaymentGroupDao.class, GroupRowToModelMapper.class})
public class PaymentGroupDaoTest extends AbstractPostgresIntegrationTest {

    public static final String GROUP_1 = "group_1";
    public static final String GROUP_2 = "group_2";
    public static final String TEST_TEMPL_2 = "test_templ_2";
    public static final String TEST_TEMPL_1 = "test_templ_1";
    public static final String UPDATE_GROUP = "UPDATE_GROUP";
    public static final String CHANGED_TEMPLATE_ID = "CHANGED_TEMPLATE_ID";

    @Autowired
    GroupDao groupDao;

    @Test
    public void insert() {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId(GROUP_1);
        groupModel.setPriorityTemplates(List.of(
                new PriorityIdModel(2L, TEST_TEMPL_1, null),
                new PriorityIdModel(1L, TEST_TEMPL_2, null)));
        groupDao.insert(groupModel);

        GroupModel byId = groupDao.getById(GROUP_1);

        assertEquals(2L, byId.getPriorityTemplates().size());

        groupModel.setGroupId(GROUP_2);
        groupDao.insert(groupModel);

        List<GroupModel> groupModels = groupDao.filterGroup(null);
        assertEquals(2, groupModels.size());

        groupModels = groupDao.filterGroup("group%");
        assertEquals(2, groupModels.size());

        groupModels = groupDao.filterGroup("test_templ_2");
        assertEquals(2, groupModels.size());

        groupDao.remove(groupModel);
        byId = groupDao.getById(GROUP_2);
        assertNull(byId.getGroupId());
    }

    @Test
    public void update() {
        GroupModel groupModel = new GroupModel();
        groupModel.setGroupId(UPDATE_GROUP);
        groupModel.setPriorityTemplates(List.of(
                new PriorityIdModel(2L, TEST_TEMPL_1, null),
                new PriorityIdModel(1L, TEST_TEMPL_2, null)));
        groupDao.insert(groupModel);

        GroupModel foundGroupModel = groupDao.getById(UPDATE_GROUP);
        assertEquals(2L, foundGroupModel.getPriorityTemplates().size());

        groupModel.getPriorityTemplates().get(0).setId(CHANGED_TEMPLATE_ID);
        groupDao.insert(groupModel);
        foundGroupModel = groupDao.getById(UPDATE_GROUP);
        assertEquals(2L, foundGroupModel.getPriorityTemplates().size());

        Optional<PriorityIdModel> template = isChangedTemplatePresent(foundGroupModel);

        groupModel.setPriorityTemplates(List.of(template.get()));
        groupDao.insert(groupModel);
        foundGroupModel = groupDao.getById(UPDATE_GROUP);
        assertEquals(1L, foundGroupModel.getPriorityTemplates().size());
        isChangedTemplatePresent(foundGroupModel);
    }

    @NotNull
    private Optional<PriorityIdModel> isChangedTemplatePresent(GroupModel foundGroupModel) {
        Optional<PriorityIdModel> template = foundGroupModel.getPriorityTemplates().stream()
                .filter(priorityIdModel -> CHANGED_TEMPLATE_ID.equals(priorityIdModel.getId()))
                .findFirst();
        assertTrue(template.isPresent());
        return template;
    }
}
