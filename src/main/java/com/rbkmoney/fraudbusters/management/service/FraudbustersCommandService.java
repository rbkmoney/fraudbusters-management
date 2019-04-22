package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class FraudbustersCommandService {

    private final KafkaTemplate kafkaTemplate;

    @Value("${kafka.topic.fraudbusters.template}")
    public String topicCommand;

    public String sendCommandSync(Command command) throws ExecutionException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        command.getCommandBody().getTemplate().setId(uuid);
        kafkaTemplate.send(topicCommand, uuid, command)
                .get();
        return uuid;
    }

}
