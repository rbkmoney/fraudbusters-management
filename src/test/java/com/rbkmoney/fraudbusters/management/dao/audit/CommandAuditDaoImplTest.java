package com.rbkmoney.fraudbusters.management.dao.audit;

import com.rbkmoney.fraudbusters.management.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.fraudbusters.management.domain.enums.CommandType;
import com.rbkmoney.fraudbusters.management.domain.enums.ObjectType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import org.jetbrains.annotations.NotNull;
import org.jooq.SortOrder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ContextConfiguration(classes = {CommandAuditDaoImpl.class})
public class CommandAuditDaoImplTest extends AbstractPostgresIntegrationTest {

    public static final String INITIATOR = "initiator";
    public static final String STRUGA = "struga";
    @Autowired
    CommandAuditDao commandAuditDao;

    @Test
    public void insert() {
        CommandAudit log = new CommandAudit();
        log.setInitiator(INITIATOR);
        log.setObject("{test}");
        log.setObjectType(ObjectType.group);
        log.setCommandType(CommandType.CREATE);
        commandAuditDao.insert(log);

        LocalDateTime now = LocalDateTime.now();
        FilterRequest filterRequest = initDefaultFilters();
        List<String> commandTypes = getCommandTypes();
        List<String> objectTypes = getObjectTypes();
        List<CommandAudit> commandAudits = commandAuditDao.filterLog(now.minusHours(10), now, commandTypes,
                objectTypes, filterRequest);

        Assert.assertEquals(1, commandAudits.size());

        //check by types
        log.setCommandType(CommandType.DELETE);
        commandAuditDao.insert(log);

        commandAudits = commandAuditDao.filterLog(now.minusHours(10), now, List.of(CommandType.CREATE.name()),
                objectTypes, filterRequest);
        Assert.assertEquals(1, commandAudits.size());

        //check by searchField
        log.setInitiator(STRUGA);
        commandAuditDao.insert(log);
        filterRequest.setSearchValue("str%");
        commandAudits = commandAuditDao.filterLog(now.minusHours(10), now, commandTypes,
                objectTypes, filterRequest);
        Assert.assertEquals(1, commandAudits.size());

        //sort
        filterRequest.setSearchValue("%");
        filterRequest.setSortOrder(SortOrder.ASC);
        commandAudits = commandAuditDao.filterLog(now.minusHours(10), now, commandTypes,
                objectTypes, filterRequest);
        Assert.assertEquals(INITIATOR, commandAudits.get(0).getInitiator());

        filterRequest.setSortOrder(SortOrder.DESC);
        commandAudits = commandAuditDao.filterLog(now.minusHours(10), now, commandTypes,
                objectTypes, filterRequest);
        Assert.assertEquals(STRUGA, commandAudits.get(0).getInitiator());
    }

    @NotNull
    private FilterRequest initDefaultFilters() {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setSize(10);
        filterRequest.setSortOrder(SortOrder.DESC);
        return filterRequest;
    }

    @NotNull
    private List<String> getCommandTypes() {
        return Arrays.stream(CommandType.values())
                .map(CommandType::name)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> getObjectTypes() {
        return Arrays.stream(ObjectType.values())
                .map(ObjectType::name)
                .collect(Collectors.toList());
    }
}
