package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.CountInfoListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsToListRecordWithRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.CountInfoListRequest;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
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
@RequiredArgsConstructor
public class WbListResource {

    private final PaymentListRecordToRowConverter paymentListRecordToRowConverter;
    private final CountInfoListRecordToRowConverter countInfoListRecordToRowConverter;
    private final WbListDao wbListDao;
    private final WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;
    private final WbListRecordsToListRecordWithRowConverter wbListRecordsToListRecordWithRowConverter;
    private final WbListCommandService wbListCommandService;

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    @PostMapping(value = "/whiteList")
    public ResponseEntity<List<String>> insertRowToWhite(@Validated @RequestBody List<PaymentListRecord> records) {
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

    private String insertInWhiteList(PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        return wbListCommandService.sendCommandSync(topicCommand, row, ListType.white, Command.CREATE);
    }

    @DeleteMapping(value = "/whiteList")
    public ResponseEntity<String> removeRowFromWhiteList(@Validated @RequestBody PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(topicCommand, row, ListType.white, Command.DELETE);
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
    public ResponseEntity<List<String>> insertRowToBlack(@RequestBody List<PaymentListRecord> records) {
        return insertInList(this::insertBlackList, records);
    }

    private String insertBlackList(PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource blackList add record {}", record);
        return wbListCommandService.sendCommandSync(topicCommand, row, ListType.black, Command.CREATE);
    }

    @DeleteMapping(value = "/blackList")
    public ResponseEntity<String> removeRowFromBlackList(@RequestBody PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(topicCommand, row, ListType.black, Command.DELETE);
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

    @PostMapping(value = "/greyList")
    public ResponseEntity<List<String>> insertRowToGrey(@RequestBody List<CountInfoListRequest> records) {
        return insertInList(this::insertGreyList, records);
    }

    private String insertGreyList(CountInfoListRequest record) {
        Row row = countInfoListRecordToRowConverter.convert(record);
        log.info("WbListResource greyList add record {}", record);
        return wbListCommandService.sendCommandSync(topicCommand, row, ListType.grey, Command.CREATE);
    }

    @DeleteMapping(value = "/greyList")
    public ResponseEntity<String> removeRowFromGreyList(@RequestBody CountInfoListRequest record) {
        Row row = countInfoListRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList add record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(topicCommand, row, ListType.grey, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/greyList")
    public ResponseEntity<List<CountInfoListRequest>> getGreyList(@RequestParam(required = false) String partyId,
                                                                  @RequestParam(required = false) String shopId,
                                                                  @RequestParam String listName) {
        List<CountInfoListRequest> listRecords = wbListDao.getFilteredListRecords(partyId, shopId, com.rbkmoney.fraudbusters.management.domain.enums.ListType.grey, listName)
                .stream()
                .map(wbListRecordsToListRecordWithRowConverter::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(listRecords);
    }

}
