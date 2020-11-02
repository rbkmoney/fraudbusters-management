package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentCountInfoRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.exception.KafkaProduceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WbListCommandService {

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    private final KafkaTemplate<String, TBase> kafkaTemplate;
    private final PaymentListRecordToRowConverter paymentListRecordToRowConverter;
    private final PaymentCountInfoRequestToRowConverter countInfoListRecordToRowConverter;

    public String sendCommandSync(Row row, ListType type, Command command) {
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

    public <T> ResponseEntity<List<String>> sendListRecords(List<T> records, ListType listType, BiFunction<T, ListType, Row> func) {
        try {
            List<String> recordIds = records.stream()
                    .map(record -> {
                        Row row = func.apply(record, listType);
                        log.info("WbListResource list add row {}", row);
                        return sendCommandSync(row, listType, Command.CREATE);
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok()
                    .body(recordIds);
        } catch (Exception e) {
            log.error("Error when insert rows: {} e: ", records, e);
            return ResponseEntity.status(500).build();
        }
    }

}
