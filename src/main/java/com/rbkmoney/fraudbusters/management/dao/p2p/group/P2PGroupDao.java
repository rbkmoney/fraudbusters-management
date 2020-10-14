package com.rbkmoney.fraudbusters.management.dao.p2p.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.P2pFGroupRecord;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.Record4;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.management.domain.tables.P2pFGroup.P2P_F_GROUP;

@Component
public class P2PGroupDao extends AbstractDao implements GroupDao {

    public P2PGroupDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void insert(GroupModel groupModel) {
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
                        rs.getString(P2P_F_GROUP.TEMPLATE_ID.getName()))
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
        return null;
    }
}
