package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.swag.fraudbusters.management.model.CommandAudit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommonAuditInternalToCommonAuditConverter implements
        Converter<List<com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit>, List<CommandAudit>> {

    @Override
    public List<CommandAudit> convert(
            List<com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit> commandAudits) {
        return commandAudits.stream()
                .map(commandAudit -> new com.rbkmoney.swag.fraudbusters.management.model.CommandAudit()
                        .commandType(CommandAudit.CommandTypeEnum.valueOf(commandAudit.getCommandType().name()))
                        .id(commandAudit.getId())
                        .initiator(commandAudit.getInitiator())
                        .insertTime(commandAudit.getInsertTime())
                        ._object(commandAudit.getObject())
                        .objectType(CommandAudit.ObjectTypeEnum.fromValue(commandAudit.getObjectType().name())))
                .collect(Collectors.toList());
    }
}
