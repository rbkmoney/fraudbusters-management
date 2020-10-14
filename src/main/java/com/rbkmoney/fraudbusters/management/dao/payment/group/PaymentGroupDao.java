package com.rbkmoney.fraudbusters.management.dao.payment.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.model.GroupPriorityRow;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FGroupRecord;
import org.jooq.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.tables.FGroup.F_GROUP;

@Component
public class PaymentGroupDao extends AbstractDao implements GroupDao {

    public PaymentGroupDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void insert(GroupModel groupModel) {
        List<Query> inserts = groupModel.getPriorityTemplates().stream()
                .map(pair -> getDslContext()
                        .insertInto(F_GROUP)
                        .columns(F_GROUP.GROUP_ID, F_GROUP.PRIORITY, F_GROUP.TEMPLATE_ID)
                        .values(groupModel.getGroupId(), pair.getPriority(), pair.getId())
                        .onConflict(F_GROUP.GROUP_ID, F_GROUP.TEMPLATE_ID)
                        .doNothing()
                ).collect(Collectors.toList());
        batchExecute(inserts);
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
        SelectConditionStep<Record4<Long, String, Long, String>> where = getDslContext()
                .select(F_GROUP.ID, F_GROUP.GROUP_ID, F_GROUP.PRIORITY, F_GROUP.TEMPLATE_ID)
                .from(F_GROUP)
                .where(F_GROUP.GROUP_ID.eq(id));
        List<PriorityIdModel> list = fetch(where, (rs, rowNum) ->
                new PriorityIdModel(
                        rs.getLong(F_GROUP.PRIORITY.getName()),
                        rs.getString(F_GROUP.TEMPLATE_ID.getName()))
        );
        GroupModel groupModel = new GroupModel();
        if (list != null && !list.isEmpty()) {
            groupModel.setGroupId(id);
            groupModel.setPriorityTemplates(list);
        }
        return groupModel;
    }

    @Override
    public List<GroupModel> filterGroup(String filterValue) {
        SelectJoinStep<Record3<String, String, Long>> from = getDslContext()
                .select(F_GROUP.GROUP_ID, F_GROUP.TEMPLATE_ID, F_GROUP.PRIORITY)
                .from(F_GROUP);
        SelectConditionStep<Record1<String>> selectGroupsId = null;
        if (!StringUtils.isEmpty(filterValue)) {
            selectGroupsId = getDslContext()
                    .selectDistinct(F_GROUP.GROUP_ID)
                    .from(F_GROUP)
                    .where(F_GROUP.GROUP_ID.like(filterValue)
                            .or(F_GROUP.TEMPLATE_ID.like(filterValue)));
        }
        List<GroupPriorityRow> list = fetch(StringUtils.isEmpty(filterValue) ? from : from.where(F_GROUP.GROUP_ID.in(selectGroupsId)),
                (rs, rowNum) ->
                        GroupPriorityRow.builder()
                                .groupId(rs.getString(F_GROUP.GROUP_ID.getName()))
                                .priorityIdModel(PriorityIdModel.builder()
                                        .id(rs.getString(F_GROUP.TEMPLATE_ID.getName()))
                                        .priority(rs.getLong(F_GROUP.PRIORITY.getName()))
                                        .build())
                                .build()
        );
        return groupByGroupId(list);
    }

    private List<GroupModel> groupByGroupId(List<GroupPriorityRow> list) {
        if (!CollectionUtils.isEmpty(list)) {
            return list.stream()
                    .collect(Collectors.groupingBy(GroupPriorityRow::getGroupId,
                            Collectors.mapping(GroupPriorityRow::getPriorityIdModel, Collectors.toList()))
                    ).entrySet().stream()
                    .map(entry -> GroupModel.builder()
                            .groupId(entry.getKey())
                            .priorityTemplates(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();

    }
}
