package com.rbkmoney.fraudbusters.management.service.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GroupModelCommandService {

    private final CommandSender commandSender;
    private final String topic;

    public String sendCommandSync(Command command) {
        String key = command.getCommandBody().getGroup().getGroupId();
        return commandSender.send(topic, command, key);
    }

}
