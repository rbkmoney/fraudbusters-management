package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateCommandService {

    private final CommandSender commandSender;

    @Value("${kafka.topic.fraudbusters.template}")
    public String topic;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getTemplate().getId();
        return commandSender.send(topic, command, key);
    }

}
