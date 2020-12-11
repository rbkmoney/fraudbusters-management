package com.rbkmoney.fraudbusters.management.service.iface;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.wb_list.Event;

public interface AuditService {

    void logCommand(Command command);

    void logEvent(Event event);

}
