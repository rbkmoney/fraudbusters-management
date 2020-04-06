package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2PWbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pCountInfoListRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pWbListRecordsToListRecordWithRowConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pCountInfo;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2PWbListResource {

    private final P2pListRecordToRowConverter p2pListRecordToRowConverter;
    private final P2pCountInfoListRequestToRowConverter countInfoListRecordToRowConverter;
    private final P2PWbListDao p2PWbListDao;
    private final P2PWbListRecordsToListRecordConverter p2PWbListRecordsToListRecordConverter;
    private final P2pWbListRecordsToListRecordWithRowConverter listRecordWithRowConverter;
    private final WbListCommandService wbListCommandService;

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    @PostMapping(value = "/whiteList")
    public ResponseEntity<List<String>> insertRowToWhite(@Validated @RequestBody List<P2pListRecord> records) {
        return insertInList(this::insertInWhiteList, records);
    }

    private <T> ResponseEntity<List<String>> insertInList(Function<T, String> func, List<T> records) {
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

    private String insertInWhiteList(P2pListRecord record) {
        Row row = p2pListRecordToRowConverter.convert(record);
        log.info("P2PWbListResource whiteList add record {}", record);
        return wbListCommandService.sendCommandSync(topicCommand, row, ListType.white, Command.CREATE);
    }

    @DeleteMapping(value = "/whiteList")
    public ResponseEntity<String> removeRowFromWhiteList(@Validated @RequestBody P2pListRecord record) {
        Row row = p2pListRecordToRowConverter.convert(record);
        log.info("P2PWbListResource whiteList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(topicCommand, row, ListType.white, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/whiteList")
    public ResponseEntity<List<ListRecord>> getWhiteList(@RequestParam(required = false) String identityId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(identityId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.white);
        return ResponseEntity.ok().body(listRecords);
    }

    @PostMapping(value = "/blackList")
    public ResponseEntity<List<String>> insertRowToBlack(@RequestBody List<P2pListRecord> records) {
        return insertInList(this::insertBlackList, records);
    }

    private String insertBlackList(P2pListRecord record) {
        Row row = p2pListRecordToRowConverter.convert(record);
        log.info("P2PWbListResource blackList add record {}", record);
        return wbListCommandService.sendCommandSync(topicCommand, row, ListType.black, Command.CREATE);
    }

    @DeleteMapping(value = "/blackList")
    public ResponseEntity<String> removeRowFromBlackList(@RequestBody P2pListRecord record) {
        Row row = p2pListRecordToRowConverter.convert(record);
        log.info("P2PWbListResource blackList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(topicCommand, row, ListType.black, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/blackList")
    public ResponseEntity<List<ListRecord>> getBlackList(@RequestParam(required = false) String identityId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(identityId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.black);
        return ResponseEntity.ok().body(listRecords);
    }

    private List<ListRecord> selectConvertedList(String identityId, String listName,
                                                 com.rbkmoney.fraudbusters.management.domain.enums.ListType type) {
        List<P2pWbListRecords> filteredListRecords = p2PWbListDao.getFilteredListRecords(identityId, type, listName);
        return filteredListRecords.stream()
                .map(p2PWbListRecordsToListRecordConverter::destinationToSource)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/greyList")
    public ResponseEntity<List<String>> insertRowToGrey(@RequestBody List<P2pCountInfo> records) {
        return insertInList(this::insertGreyList, records);
    }

    private String insertGreyList(P2pCountInfo record) {
        Row row = countInfoListRecordToRowConverter.convert(record);
        log.info("P2PWbListResource greyList add record {}", record);
        return wbListCommandService.sendCommandSync(topicCommand, row, ListType.grey, Command.CREATE);
    }

    @DeleteMapping(value = "/greyList")
    public ResponseEntity<String> removeRowFromGreyList(@RequestBody P2pCountInfo record) {
        Row row = countInfoListRecordToRowConverter.convert(record);
        log.info("P2PWbListResource greyList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(topicCommand, row, ListType.grey, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/greyList")
    public ResponseEntity<List<P2pCountInfo>> getGreyList(@RequestParam(required = false) String identityId,
                                                          @RequestParam String listName) {
        List<P2pCountInfo> listRecords = p2PWbListDao.getFilteredListRecords(identityId, com.rbkmoney.fraudbusters.management.domain.enums.ListType.grey, listName)
                .stream()
                .map(listRecordWithRowConverter::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(listRecords);
    }

}
