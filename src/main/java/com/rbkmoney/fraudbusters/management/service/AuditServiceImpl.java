package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.converter.CommandToAuditLogConverter;
import com.rbkmoney.fraudbusters.management.converter.CommonAuditInternalToCommonAuditConverter;
import com.rbkmoney.fraudbusters.management.converter.EventToAuditLogConverter;
import com.rbkmoney.fraudbusters.management.dao.audit.CommandAuditDao;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.FilterLogsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final CommandAuditDao commandAuditDao;
    private final CommandToAuditLogConverter commandToAuditLogConverter;
    private final EventToAuditLogConverter eventToAuditLogConverter;
    private final CommonAuditInternalToCommonAuditConverter commonAuditInternalToCommonAuditConverter;

    @Override
    public FilterLogsResponse filterRecords(List<String> commandTypes, List<String> objectTypes, String from,
                                            String to, FilterRequest filterRequest) {
        var fromDate = LocalDateTime.parse(from, DateTimeUtils.DATE_TIME_FORMATTER);
        var toDate = LocalDateTime.parse(to, DateTimeUtils.DATE_TIME_FORMATTER);
        List<CommandAudit> commandAudits = commandAuditDao.filterLog(fromDate, toDate, commandTypes,
                objectTypes, filterRequest);
        Integer count = commandAuditDao.countFilterRecords(fromDate, toDate, commandTypes,
                objectTypes, filterRequest);
        return new FilterLogsResponse()
                .count(count)
                .result(commonAuditInternalToCommonAuditConverter.convert(commandAudits));
    }

    @Override
    public void logCommand(Command command) {
        CommandAudit commandAudit = commandToAuditLogConverter.convert(command);
        commandAuditDao.insert(commandAudit);
    }

    @Override
    public void logEvent(Event command) {
        CommandAudit commandAudit = eventToAuditLogConverter.convert(command);
        commandAuditDao.insert(commandAudit);
    }

}
