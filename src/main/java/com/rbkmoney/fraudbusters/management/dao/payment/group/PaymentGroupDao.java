package com.rbkmoney.fraudbusters.management.dao.payment.group;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.dao.GroupDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import com.rbkmoney.fraudbusters.management.domain.PriorityIdModel;
import com.rbkmoney.fraudbusters.management.domain.tables.records.FGroupRecord;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.Record4;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
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
}
