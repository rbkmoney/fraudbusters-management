package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.converter.CommandToAuditLogConverter;
import com.rbkmoney.fraudbusters.management.converter.EventToAuditLogConverter;
import com.rbkmoney.fraudbusters.management.dao.audit.CommandAuditDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final CommandAuditDao commandAuditDao;
    private final CommandToAuditLogConverter commandToAuditLogConverter;
    private final EventToAuditLogConverter eventToAuditLogConverter;

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
