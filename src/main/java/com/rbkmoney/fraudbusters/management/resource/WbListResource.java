package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WbListResource {

    private final ListRecordToRowConverter listRecordToRowConverter;
    private final WbListDao wbListDao;
    private final WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;
    private final WbListCommandService wbListCommandService;

    @PostMapping(value = "/whiteList")
    public ResponseEntity<List<String>> insertRowToWhite(@Validated @RequestBody List<ListRecord> records) {
        return insertInList(this::insertInWhiteList, records);
    }

    private ResponseEntity<List<String>> insertInList(Function<ListRecord, String> func, List<ListRecord> records) {
        try {
            List<String> recordIds = records.stream()
                    .map(func)
                    .collect(Collectors.toList());
            return ResponseEntity.ok()
                    .body(recordIds);
        } catch (Exception e) {
            log.error("Error when insert rows: {} e: ", records, e);
            return ResponseEntity.status(500).build();
        }
    }

    private String insertInWhiteList(ListRecord record) {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList add record {}", record);
        return wbListCommandService.sendCommandSync(row, ListType.white, Command.CREATE);
    }

    @DeleteMapping(value = "/whiteList")
    public ResponseEntity<String> removeRowFromWhiteList(@Validated @RequestBody ListRecord record) {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(row, ListType.white, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/whiteList")
    public ResponseEntity<List<ListRecord>> getWhiteList(@RequestParam(required = false) String partyId,
                                                         @RequestParam(required = false) String shopId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(partyId, shopId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.white);
        return ResponseEntity.ok().body(listRecords);
    }

    @PostMapping(value = "/blackList")
    public ResponseEntity<List<String>> insertRowToBlack(@RequestBody List<ListRecord> records) {
        return insertInList(this::insertBlackList, records);

    }

    private String insertBlackList(ListRecord record) {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource blackList add record {}", record);
        return wbListCommandService.sendCommandSync(row, ListType.black, Command.CREATE);
    }

    @DeleteMapping(value = "/blackList")
    public ResponseEntity<String> removeRowFromBlackList(@RequestBody ListRecord record) {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList add record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(row, ListType.black, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/blackList")
    public ResponseEntity<List<ListRecord>> getBlackList(@RequestParam(required = false) String partyId,
                                                         @RequestParam(required = false) String shopId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(partyId, shopId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.black);
        return ResponseEntity.ok().body(listRecords);
    }

    private List<ListRecord> selectConvertedList(String partyId, String shopId, String listName,
                                                 com.rbkmoney.fraudbusters.management.domain.enums.ListType type) {
        List<WbListRecords> filteredListRecords = wbListDao.getFilteredListRecords(partyId, shopId, type, listName);
        return filteredListRecords.stream()
                .map(wbListRecordsToListRecordConverter::destinationToSource)
                .collect(Collectors.toList());
    }

}
