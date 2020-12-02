package com.rbkmoney.fraudbusters.management.dao.p2p.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.model.GroupPriorityRow;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFGroupRecord;
import com.rbkmoney.fraudbusters.management.utils.GroupRowToModelMapper;
import org.jooq.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFGroup.P2P_F_GROUP;

@Component
public class P2PGroupDao extends AbstractDao implements GroupDao {

    private final GroupRowToModelMapper groupRowToModelMapper;

    public P2PGroupDao(DataSource dataSource, GroupRowToModelMapper groupRowToModelMapper) {
        super(dataSource);
        this.groupRowToModelMapper = groupRowToModelMapper;
    }

    @Override
    @Transactional
    public void insert(GroupModel groupModel) {
        cleanRemovedTemplates(groupModel);
        List<Query> inserts = groupModel.getPriorityTemplates().stream()
                .map(pair -> getDslContext()
                        .insertInto(P2P_F_GROUP)
                        .columns(P2P_F_GROUP.GROUP_ID, P2P_F_GROUP.PRIORITY, P2P_F_GROUP.TEMPLATE_ID)
                        .values(groupModel.getGroupId(), pair.getPriority(), pair.getId())
                        .onConflict(P2P_F_GROUP.GROUP_ID, P2P_F_GROUP.TEMPLATE_ID)
                        .doNothing()
                ).collect(Collectors.toList());
        batchExecute(inserts);
    }

    private void cleanRemovedTemplates(GroupModel groupModel) {
        DeleteConditionStep<P2pFGroupRecord> where = getDslContext()
                .delete(P2P_F_GROUP)
                .where(P2P_F_GROUP.GROUP_ID.eq(groupModel.getGroupId())
                        .and(P2P_F_GROUP.TEMPLATE_ID.notIn(groupModel.getPriorityTemplates().stream()
                                .map(PriorityIdModel::getId)
                                .collect(Collectors.toList()))));
        execute(where);
    }

    @Override
    public void remove(String id) {
        DeleteConditionStep<P2pFGroupRecord> where = getDslContext()
                .delete(P2P_F_GROUP)
                .where(P2P_F_GROUP.GROUP_ID.eq(id));
        execute(where);
    }

    @Override
    public void remove(GroupModel groupModel) {
        DeleteConditionStep<P2pFGroupRecord> where = getDslContext()
                .delete(P2P_F_GROUP)
                .where(P2P_F_GROUP.GROUP_ID.eq(groupModel.getGroupId()));
        execute(where);
    }

    @Override
    public GroupModel getById(String id) {
        SelectConditionStep<Record4<Long, String, Long, String>> where = getDslContext()
                .select(P2P_F_GROUP.ID, P2P_F_GROUP.GROUP_ID, P2P_F_GROUP.PRIORITY, P2P_F_GROUP.TEMPLATE_ID)
                .from(P2P_F_GROUP)
                .where(P2P_F_GROUP.GROUP_ID.eq(id));
        List<PriorityIdModel> list = fetch(where, (rs, rowNum) ->
                new PriorityIdModel(
                        rs.getLong(P2P_F_GROUP.PRIORITY.getName()),
                        rs.getString(P2P_F_GROUP.TEMPLATE_ID.getName()),
                        null)
        );
        return CollectionUtils.isEmpty(list)
                ? GroupModel.builder().groupId(id).priorityTemplates(list).build()
                : null;
    }

    @Override
    public List<GroupModel> filterGroup(String filterValue) {
        SelectJoinStep<Record3<String, String, Long>> from = getDslContext()
                .select(P2P_F_GROUP.GROUP_ID, P2P_F_GROUP.TEMPLATE_ID, P2P_F_GROUP.PRIORITY)
                .from(P2P_F_GROUP);
        SelectConditionStep<Record1<String>> selectGroupsId = null;
        if (!StringUtils.isEmpty(filterValue)) {
            selectGroupsId = getDslContext()
                    .selectDistinct(P2P_F_GROUP.GROUP_ID)
                    .from(P2P_F_GROUP)
                    .where(P2P_F_GROUP.GROUP_ID.like(filterValue)
                            .or(P2P_F_GROUP.TEMPLATE_ID.like(filterValue)));
        }
        List<GroupPriorityRow> list = fetch(StringUtils.isEmpty(filterValue) ? from : from.where(P2P_F_GROUP.GROUP_ID.in(selectGroupsId)),
                (rs, rowNum) -> createGroupPriorityRow(rs)
        );
        return groupRowToModelMapper.groupByGroupId(list);
    }

    private GroupPriorityRow createGroupPriorityRow(ResultSet rs) throws SQLException {
        return GroupPriorityRow.builder()
                .groupId(rs.getString(P2P_F_GROUP.GROUP_ID.getName()))
                .priorityIdModel(PriorityIdModel.builder()
                        .id(rs.getString(P2P_F_GROUP.TEMPLATE_ID.getName()))
                        .lastUpdateTime(rs.getString(P2P_F_GROUP.LAST_UPDATE_DATE.getName()))
                        .priority(rs.getLong(P2P_F_GROUP.PRIORITY.getName()))
                        .build())
                .build();
    }
}
