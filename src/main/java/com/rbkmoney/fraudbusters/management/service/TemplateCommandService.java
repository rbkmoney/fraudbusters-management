package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TemplateCommandService {

    private final CommandSender commandSender;
    private final String topic;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getTemplate().getId();
        return commandSender.send(topic, command, key);
    }

}
