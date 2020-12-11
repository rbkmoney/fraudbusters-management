package com.rbkmoney.fraudbusters.management.dao.audit;

import com.rbkmoney.fraudbusters.management.dao.AbstractDao;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import com.rbkmoney.fraudbusters.management.domain.tables.records.CommandAuditRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fraudbusters.management.domain.tables.CommandAudit.COMMAND_AUDIT;

@Component
public class CommandAuditDaoImpl extends AbstractDao implements CommandAuditDao {

    private final RowMapper<CommandAudit> listRecordRowMapper;

    public CommandAuditDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(COMMAND_AUDIT, CommandAudit.class);
    }

    @Override
    public void insert(CommandAudit log) {
        execute(getDslContext().insertInto(COMMAND_AUDIT)
                .set(getDslContext().newRecord(COMMAND_AUDIT, log)));
    }

    @Override
    public List<CommandAudit> filterLog(LocalDateTime from,
                                        LocalDateTime to,
                                        List<String> commandTypes,
                                        List<String> objectTypes,
                                        FilterRequest filterRequest) {
        SelectWhereStep<CommandAuditRecord> select = getDslContext()
                .selectFrom(COMMAND_AUDIT);
        Condition condition = COMMAND_AUDIT.COMMAND_TYPE.in(commandTypes).and(COMMAND_AUDIT.OBJECT_TYPE.in(objectTypes));
        SelectConditionStep<CommandAuditRecord> where = StringUtils.isEmpty(filterRequest.getSearchValue()) ?
                select.where(condition.and(COMMAND_AUDIT.INSERT_TIME.between(from, to))) :
                select.where(condition.and(
                        COMMAND_AUDIT.INITIATOR.like(filterRequest.getSearchValue()))
                        .and(COMMAND_AUDIT.INSERT_TIME.between(from, to)));
        Field field = StringUtils.isEmpty(filterRequest.getSortBy()) ?
                COMMAND_AUDIT.INSERT_TIME :
                COMMAND_AUDIT.field(filterRequest.getSortBy());
        SelectSeekStep2<CommandAuditRecord, Object, String> auditRecords = addSortCondition(COMMAND_AUDIT.ID,
                field, filterRequest.getSortOrder(), where);
        return fetch(
                addSeekIfNeed(
                        filterRequest.getLastId(),
                        filterRequest.getSortFieldValue(),
                        filterRequest.getSize(),
                        auditRecords),
                listRecordRowMapper
        );
    }

    @Override
    public Integer countFilterRecords(LocalDateTime from,
                                      LocalDateTime to,
                                      List<String> commandTypes,
                                      List<String> objectTypes,
                                      FilterRequest filterRequest) {
        Condition condition = COMMAND_AUDIT.COMMAND_TYPE.in(commandTypes).and(COMMAND_AUDIT.OBJECT_TYPE.in(objectTypes));
        SelectConditionStep<Record1<Integer>> where = getDslContext()
                .selectCount()
                .from(COMMAND_AUDIT)
                .where( StringUtils.isEmpty(filterRequest.getSearchValue()) ?
                        condition.and(COMMAND_AUDIT.INSERT_TIME.between(from, to)) :
                        condition.and(
                                COMMAND_AUDIT.INITIATOR.like(filterRequest.getSearchValue()))
                                .and(COMMAND_AUDIT.INSERT_TIME.between(from, to)));
        return fetchOne(where, Integer.class);
    }

}
