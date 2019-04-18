package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.service.CommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WbListResource {

    private final ListRecordToRowConverter listRecordToRowConverter;
    private final WbListDao wbListDao;
    private final WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;
    private final CommandService commandService;

    @PostMapping(value = "/whiteList")
    public ResponseEntity<String> insertRowToWhite(@RequestBody ListRecord record) throws ExecutionException, InterruptedException {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList add record {}", record);
        String idMessage = commandService.sendCommandSync(row, ListType.white, Command.CREATE);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/whiteList")
    public ResponseEntity<String> removeRowFromWhiteList(@RequestBody ListRecord record) throws ExecutionException, InterruptedException {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList remove record {}", record);
        String idMessage = commandService.sendCommandSync(row, ListType.white, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/whiteList")
    public ResponseEntity<List<ListRecord>> getWhiteList(@RequestParam String partyId,
                                                         @RequestParam String shopId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(partyId, shopId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.white);
        return ResponseEntity.ok().body(listRecords);
    }

    @PostMapping(value = "/blackList")
    public ResponseEntity<String> insertRowToBlack(@RequestBody ListRecord record) throws ExecutionException, InterruptedException {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList add record {}", record);
        String idMessage = commandService.sendCommandSync(row, ListType.black, Command.CREATE);
        return ResponseEntity.ok().body(idMessage);
    }

    @DeleteMapping(value = "/blackList")
    public ResponseEntity<String> removeRowFromBlackList(@RequestBody ListRecord record) throws ExecutionException, InterruptedException {
        Row row = listRecordToRowConverter.destinationToSource(record);
        log.info("WbListResource whiteList add record {}", record);
        String idMessage = commandService.sendCommandSync(row, ListType.black, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/blackList")
    public ResponseEntity<List<ListRecord>> getBlackList(@RequestParam String partyId,
                                                         @RequestParam String shopId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(partyId, shopId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.black);
        return ResponseEntity.ok().body(listRecords);
    }

    private List<ListRecord> selectConvertedList(String partyId, String shopId, String listName,
                                                 com.rbkmoney.fraudbusters.management.domain.enums.ListType black) {
        List<WbListRecords> filteredListRecords = wbListDao.getFilteredListRecords(partyId, shopId,
                black, listName);
        return filteredListRecords.stream()
                .map(wbListRecordsToListRecordConverter::destinationToSource)
                .collect(Collectors.toList());
    }

}
