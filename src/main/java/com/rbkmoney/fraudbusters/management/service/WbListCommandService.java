package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentCountInfoRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.exception.KafkaProduceException;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
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

    public ResponseEntity<List<String>> sendListRecords(List<PaymentCountInfo> records, ListType listType) {
        try {
            List<String> recordIds = records.stream()
                    .map(t -> mapToRow(t, listType))
                    .collect(Collectors.toList());
            return ResponseEntity.ok()
                    .body(recordIds);
        } catch (Exception e) {
            log.error("Error when insert rows: {} e: ", records, e);
            return ResponseEntity.status(500).build();
        }
    }

    private String mapToRow(PaymentCountInfo record, ListType listType) {
        Row row = initRow(record, listType);
        log.info("WbListResource list add row {}", row);
        return sendCommandSync(row, listType, Command.CREATE);
    }

    private Row initRow(PaymentCountInfo record, ListType listType) {
        Row row = null;
        switch (listType) {
            case black:
            case white:
                row = paymentListRecordToRowConverter.convert(record.getListRecord());
                break;
            case grey:
                row = countInfoListRecordToRowConverter.convert(record);
                break;
            default:
                throw new UnknownEventException();
        }
        return row;
    }


}
