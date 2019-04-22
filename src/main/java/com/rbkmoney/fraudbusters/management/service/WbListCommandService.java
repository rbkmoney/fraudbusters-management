package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class WbListCommandService {

    private final KafkaTemplate kafkaTemplate;

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    public String sendCommandSync(Row row, ListType type, Command command) throws ExecutionException, InterruptedException {
        row.setListType(type);
        String uuid = UUID.randomUUID().toString();
        kafkaTemplate.send(topicCommand, uuid, createChangeCommand(row, command))
                .get();
        return uuid;
    }

    private ChangeCommand createChangeCommand(Row row, Command command) {
        ChangeCommand changeCommand = new ChangeCommand();
        changeCommand.setRow(row);
        changeCommand.setCommand(command);
        return changeCommand;
    }

}
