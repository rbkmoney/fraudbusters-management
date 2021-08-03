package com.rbkmoney.fraudbusters.management.dao.payment.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.model.GroupPriorityRow;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FGroupRecord;
import com.rbkmoney.fraudbusters.management.utils.GroupRowToModelMapper;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.tables.FGroup.F_GROUP;

@Component
public class PaymentGroupDao extends AbstractDao implements GroupDao {

    private final GroupRowToModelMapper groupRowToModelMapper;

    public PaymentGroupDao(DataSource dataSource, @Autowired GroupRowToModelMapper groupRowToModelMapper) {
        super(dataSource);
        this.groupRowToModelMapper = groupRowToModelMapper;
    }

    @Override
    @Transactional
    public void insert(GroupModel groupModel) {
        cleanRemovedTemplates(groupModel);
        List<Query> inserts = groupModel.getPriorityTemplates().stream()
                .map(pair -> getDslContext()
                        .insertInto(F_GROUP)
                        .columns(F_GROUP.GROUP_ID, F_GROUP.PRIORITY, F_GROUP.TEMPLATE_ID, F_GROUP.MODIFIED_BY_USER)
                        .values(groupModel.getGroupId(), pair.getPriority(), pair.getId(),
                                groupModel.getModifiedByUser())
                        .onConflict(F_GROUP.GROUP_ID, F_GROUP.TEMPLATE_ID)
                        .doNothing()
                ).collect(Collectors.toList());
        batchExecute(inserts);
    }

    private void cleanRemovedTemplates(GroupModel groupModel) {
        DeleteConditionStep<FGroupRecord> where = getDslContext()
                .delete(F_GROUP)
                .where(F_GROUP.GROUP_ID.eq(groupModel.getGroupId())
                        .and(F_GROUP.TEMPLATE_ID.notIn(groupModel.getPriorityTemplates().stream()
                                .map(PriorityIdModel::getId)
                                .collect(Collectors.toList()))));
        execute(where);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<FGroupRecord> where = getDslContext()
                .delete(F_GROUP)
                .where(F_GROUP.GROUP_ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(GroupModel groupModel) {
        DeleteConditionStep<FGroupRecord> where = getDslContext()
                .delete(F_GROUP)
                .where(F_GROUP.GROUP_ID.eq(groupModel.getGroupId()));
        execute(where);
    }

    @Override
    public GroupModel getById(String id) {
        SelectConditionStep<Record6<Long, String, Long, String, String, LocalDateTime>> where = getDslContext()
                .select(F_GROUP.ID, F_GROUP.GROUP_ID, F_GROUP.PRIORITY, F_GROUP.TEMPLATE_ID, F_GROUP.MODIFIED_BY_USER,
                        F_GROUP.LAST_UPDATE_DATE)
                .from(F_GROUP)
                .where(F_GROUP.GROUP_ID.eq(id));
        List<PriorityIdModel> list = fetch(where, (rs, rowNum) ->
                PriorityIdModel.builder()
                        .priority(rs.getLong(F_GROUP.PRIORITY.getName()))
                        .lastUpdateTime(rs.getTimestamp(F_GROUP.LAST_UPDATE_DATE.getName()).toLocalDateTime())
                        .id(rs.getString(F_GROUP.TEMPLATE_ID.getName())).build()
        );
        return !CollectionUtils.isEmpty(list)
                ? GroupModel.builder().groupId(id).priorityTemplates(list).build()
                : null;
    }

    @Override
    public List<GroupModel> filterGroup(String filterValue) {
        SelectJoinStep<Record4<String, String, Long, LocalDateTime>> from = getDslContext()
                .select(F_GROUP.GROUP_ID, F_GROUP.TEMPLATE_ID, F_GROUP.PRIORITY, F_GROUP.LAST_UPDATE_DATE)
                .from(F_GROUP);
        SelectConditionStep<Record1<String>> selectGroupsId = null;
        if (!StringUtils.isEmpty(filterValue)) {
            selectGroupsId = getDslContext()
                    .selectDistinct(F_GROUP.GROUP_ID)
                    .from(F_GROUP)
                    .where(F_GROUP.GROUP_ID.like(filterValue)
                            .or(F_GROUP.TEMPLATE_ID.like(filterValue)));
        }
        List<GroupPriorityRow> list =
                fetch(StringUtils.isEmpty(filterValue) ? from : from.where(F_GROUP.GROUP_ID.in(selectGroupsId)),
                        (rs, rowNum) -> createGroupPriorityRow(rs)
                );
        return groupRowToModelMapper.groupByGroupId(list);
    }

    private GroupPriorityRow createGroupPriorityRow(ResultSet rs) throws SQLException {
        return GroupPriorityRow.builder()
                .groupId(rs.getString(F_GROUP.GROUP_ID.getName()))
                .priorityIdModel(PriorityIdModel.builder()
                        .id(rs.getString(F_GROUP.TEMPLATE_ID.getName()))
                        .lastUpdateTime(rs.getTimestamp(F_GROUP.LAST_UPDATE_DATE.getName()).toLocalDateTime())
                        .priority(rs.getLong(F_GROUP.PRIORITY.getName()))
                        .build())
                .build();
    }

}
