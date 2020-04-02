package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.exception.KafkaProduceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WbListCommandService {

    private final KafkaTemplate<String, TBase> kafkaTemplate;

    public String sendCommandSync(String topicCommand, Row row, ListType type, Command command) {
        row.setListType(type);
        String uuid = UUID.randomUUID().toString();
        try {
            ChangeCommand changeCommand = createChangeCommand(row, command);
            kafkaTemplate.send(topicCommand, uuid, changeCommand)
                    .get();
            log.info("WbListCommandService sent command: {}", changeCommand);
        } catch (InterruptedException e) {
            log.error("InterruptedException e: ", e);
            Thread.currentThread().interrupt();
            throw new KafkaProduceException(e);
        } catch (Exception e) {
            log.error("Error when send e: ", e);
            throw new KafkaProduceException(e);
        }
        return uuid;
    }

    private ChangeCommand createChangeCommand(Row row, Command command) {
        ChangeCommand changeCommand = new ChangeCommand();
        changeCommand.setRow(row);
        changeCommand.setCommand(command);
        return changeCommand;
    }

}
