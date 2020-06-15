package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.utils.ReferenceKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTemplateReferenceService {

    private final CommandSender commandSender;

    @Value("${kafka.topic.fraudbusters.reference}")
    public String topic;

    public String sendCommandSync(Command command) {
        String key = ReferenceKeyGenerator.generateTemplateKey(command.getCommandBody().getReference());
        return commandSender.send(topic, command, key);
    }

}
