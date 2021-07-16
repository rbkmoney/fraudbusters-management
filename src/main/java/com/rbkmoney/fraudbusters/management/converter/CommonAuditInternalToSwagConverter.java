package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.swag.fraudbusters.management.model.CommandAudit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommonAuditInternalToSwagConverter implements
        Converter<List<com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit>, List<CommandAudit>> {

    @Override
    public List<CommandAudit> convert(
            List<com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit> commandAudits) {
        return commandAudits.stream()
                .map(commandAudit -> new com.rbkmoney.swag.fraudbusters.management.model.CommandAudit()
                        .commandType(CommandAudit.CommandTypeEnum.valueOf(commandAudit.getCommandType().getName()))
                        .id(commandAudit.getId())
                        .initiator(commandAudit.getInitiator())
                        .insertTime(commandAudit.getInsertTime())
                        .objectType(CommandAudit.ObjectTypeEnum.valueOf(commandAudit.getObjectType().getName())))
                .collect(Collectors.toList());
    }
}
