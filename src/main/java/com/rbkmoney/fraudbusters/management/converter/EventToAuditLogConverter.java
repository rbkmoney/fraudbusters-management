package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.domain.enums.CommandType;
import com.rbkmoney.fraudbusters.management.domain.enums.ObjectType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventToAuditLogConverter implements Converter<Event, CommandAudit> {

    public static final String UNKNOWN = "UNKNOWN";

    @Override
    public CommandAudit convert(Event event) {
        CommandAudit model = new CommandAudit();
        model.setCommandType(parseEventType(event));
        model.setObjectType(ObjectType.valueOf(event.getRow().getListType().name()));
        model.setObject(event.getRow().toString());
        model.setInitiator(event.getUserInfo() != null && !StringUtils.isEmpty(event.getUserInfo().getUserId())
                ? event.getUserInfo().getUserId()
                : UNKNOWN);
        return model;
    }

    private CommandType parseEventType(Event event) {
        switch (event.getEventType()) {
            case CREATED:
                return CommandType.CREATE;
            case DELETED:
                return CommandType.DELETE;
            default:
                log.warn("CommandAudit convert unknown EventType: {}", event.getEventType());
        }
        return null;
    }
}
