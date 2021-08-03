package com.rbkmoney.fraudbusters.management.service.iface;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.swag.fraudbusters.management.model.FilterLogsResponse;

import java.util.List;

public interface AuditService {

    void logCommand(Command command);

    void logEvent(Event event);

    FilterLogsResponse filterRecords(List<String> commandTypes, List<String> objectTypes, String from,
                                     String to, FilterRequest filterRequest);
}
