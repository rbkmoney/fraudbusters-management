package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GroupCommandService {

    public final String topic;
    private final CommandSender commandSender;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getGroup().getGroupId();
        return commandSender.send(topic, command, key);
    }

}
