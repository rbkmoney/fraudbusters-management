package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RequiredArgsConstructor
public class TemplateCommandService {

    public static final String EMPTY_STRING = "";

    private final CommandSender commandSender;
    private final String topic;
    private final TemplateModelToCommandConverter templateModelToCommandConverter;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getTemplate().getId();
        return commandSender.send(topic, command, key);
    }

    public Command createTemplateCommandById(@PathVariable("id") String id) {
        return templateModelToCommandConverter.convert(TemplateModel.builder()
                .id(id)
                .template(EMPTY_STRING)
                .build());
    }

}
