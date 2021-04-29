package com.rbkmoney.fraudbusters.management.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.domain.enums.CommandType;
import com.rbkmoney.fraudbusters.management.domain.enums.ObjectType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import com.rbkmoney.geck.common.util.TBaseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandToAuditLogConverter implements Converter<Command, CommandAudit> {

    public static final String UNKNOWN = "UNKNOWN";
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CommandAudit convert(Command command) {
        CommandAudit model = new CommandAudit();
        model.setCommandType(CommandType.valueOf(command.getCommandType().name()));
        model.setObjectType(TBaseUtil.unionFieldToEnum(command.getCommandBody(), ObjectType.class));
        model.setObject(command.getCommandBody().getFieldValue().toString());
        model.setInitiator(command.getUserInfo() != null && !StringUtils.isEmpty(command.getUserInfo().getUserId())
                ? command.getUserInfo().getUserId()
                : UNKNOWN);
        return model;
    }
}
