package com.rbkmoney.fraudbusters.management.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.domain.enums.CommandType;
import com.rbkmoney.fraudbusters.management.domain.enums.ObjectType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventToAuditLogConverter implements Converter<Event, CommandAudit> {

    public static final String UNKNOWN = "UNKNOWN";
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CommandAudit convert(Event event) {
        CommandAudit model = new CommandAudit();
        model.setCommandType(parseEventType(event));
        model.setObjectType(ObjectType.valueOf(event.getRow().getListType().name()));
        try {
            model.setObject(objectMapper.writeValueAsString(event.getRow()));
        } catch (JsonProcessingException e) {
            log.error("Error when convert command: {}", event);
            model.setObject(e.getMessage());
        }
        model.setInitiator(event.getUserInfo() != null && !StringUtils.isEmpty(event.getUserInfo().getUserId()) ?
                event.getUserInfo().getUserId() : UNKNOWN);
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
