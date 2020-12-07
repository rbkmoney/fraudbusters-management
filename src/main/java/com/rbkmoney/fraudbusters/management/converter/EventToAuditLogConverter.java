package com.rbkmoney.fraudbusters.management.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.ChangeCommand;
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
    public CommandAudit convert(Event command) {
        CommandAudit model = new CommandAudit();
        model.setCommandType(CommandType.valueOf(command.getEventType().name()));
        model.setObjectType(ObjectType.valueOf(command.getRow().getListType().name()));
        try {
            model.setObject(objectMapper.writeValueAsString(command.getRow()));
        } catch (JsonProcessingException e) {
            log.error("Error when convert command: {}", command);
            model.setObject(e.getMessage());
        }
        //TODO proto
//        model.setInitiator(command.isSetUserInfo() && StringUtils.isEmpty(command.getUserInfo().getUserId()) ?
//                command.getUserInfo().getUserId() : UNKNOWN);
        return model;
    }
}
