package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WbListResource {

    private final ListRecordToRowConverter listRecordToRowConverter;
    private final KafkaTemplate kafkaTemplate;
    private final WbListDao wbListDao;
    private final WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;

    @Value("${kafka.wblist.topic.command}")
    public String topicCommand;

    @PostMapping(value = "/whiteList")
    public ResponseEntity<String> insertRowToWhite(@RequestBody ListRecord record) {
        Row row = listRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        return sendCommand(row, ListType.white, Command.CREATE);
    }

    @DeleteMapping(value = "/whiteList")
    public ResponseEntity<String> removeRowFromWhiteList(@RequestBody ListRecord record) {
        Row row = listRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        return sendCommand(row, ListType.white, Command.DELETE);
    }

    @GetMapping(value = "/whiteList")
    public ResponseEntity<List<ListRecord>> getWhiteList(@RequestParam String partyId,
                                                         @RequestParam String shopId,
                                                         @RequestParam String listName) {
        try {
            List<WbListRecords> filteredListRecords = wbListDao.getFilteredListRecords(partyId, shopId,
                    com.rbkmoney.fraudbusters.management.domain.enums.ListType.white, listName);
            List<ListRecord> listRecords = filteredListRecords.stream()
                    .map(wbListRecordsToListRecordConverter::destinationToSource)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(listRecords);
        } catch (Exception e) {
            log.error("Unexpected error when build payment request", e);
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    @PostMapping(value = "/blackList")
    public ResponseEntity<String> insertRowToBlack(@RequestBody ListRecord record) {
        Row row = listRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        return sendCommand(row, ListType.black, Command.CREATE);
    }

    @DeleteMapping(value = "/blackList")
    public ResponseEntity<String> removeRowFromBlackList(@RequestBody ListRecord record) {
        Row row = listRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        return sendCommand(row, ListType.black, Command.DELETE);
    }

    @GetMapping(value = "/blackList")
    public ResponseEntity<List<ListRecord>> getBlackList(@RequestParam String partyId,
                                                         @RequestParam String shopId,
                                                         @RequestParam String listName) {
        try {
            List<WbListRecords> filteredListRecords = wbListDao.getFilteredListRecords(partyId, shopId,
                    com.rbkmoney.fraudbusters.management.domain.enums.ListType.black, listName);
            List<ListRecord> listRecords = filteredListRecords.stream()
                    .map(wbListRecordsToListRecordConverter::destinationToSource)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(listRecords);
        } catch (Exception e) {
            log.error("Unexpected error when build payment request", e);
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    private ResponseEntity<String> sendCommand(Row row, ListType white, Command command) {
        try {
            row.setListType(white);
            kafkaTemplate.send(topicCommand, createChangeCommand(row, command));
            return ResponseEntity.ok().body("");
        } catch (Exception e) {
            log.error("Unexpected error when build payment request", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ChangeCommand createChangeCommand(Row row, Command command) {
        ChangeCommand changeCommand = new ChangeCommand();
        changeCommand.setRow(row);
        changeCommand.setCommand(command);
        return changeCommand;
    }

}
