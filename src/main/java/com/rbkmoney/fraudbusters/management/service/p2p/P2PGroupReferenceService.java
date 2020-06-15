package com.rbkmoney.fraudbusters.management.service.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.P2PGroupReference;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.utils.P2PReferenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PGroupReferenceService {

    private final CommandSender commandSender;

    @Value("${kafka.topic.fraudbusters.group.reference}")
    public String topic;

    public String sendCommandSync(Command command) {
        P2PGroupReference p2pGroupReference = command.getCommandBody().getP2pGroupReference();
        String key = P2PReferenceKeyGenerator.generateTemplateKey(p2pGroupReference.getIdentityId());
        return commandSender.send(topic, command, key);
    }

}
