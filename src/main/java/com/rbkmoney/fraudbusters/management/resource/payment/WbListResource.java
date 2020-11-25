package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentCountInfoRequestToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsToCountInfoListRequestConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Deprecated
@RestController
@RequiredArgsConstructor
public class WbListResource {

    private final PaymentListRecordToRowConverter paymentListRecordToRowConverter;
    private final PaymentCountInfoRequestToRowConverter countInfoListRecordToRowConverter;
    private final WbListDao wbListDao;
    private final WbListRecordsToListRecordConverter wbListRecordsToListRecordConverter;
    private final WbListRecordsToCountInfoListRequestConverter wbListRecordsToListRecordWithRowConverter;
    private final WbListCommandService wbListCommandService;

    @Value("${kafka.topic.wblist.command}")
    public String topicCommand;

    @PostMapping(value = "/whiteList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<String>> insertRowToWhite(@Validated @RequestBody List<PaymentListRecord> records) {
        return insertInList(this::insertInWhiteList, records);
    }

    @DeleteMapping(value = "/whiteList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<String> removeRowFromWhiteList(@Validated @RequestBody PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource whiteList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(row, ListType.white, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/whiteList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<ListRecord>> getWhiteList(@RequestParam(required = false) String partyId,
                                                         @RequestParam(required = false) String shopId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(partyId, shopId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.white);
        return ResponseEntity.ok().body(listRecords);
    }

    @PostMapping(value = "/blackList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<String>> insertRowToBlack(@RequestBody List<PaymentListRecord> records) {
        return insertInList(this::insertBlackList, records);
    }

    @DeleteMapping(value = "/blackList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<String> removeRowFromBlackList(@RequestBody PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource blackList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(row, ListType.black, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/blackList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<ListRecord>> getBlackList(@RequestParam(required = false) String partyId,
                                                         @RequestParam(required = false) String shopId,
                                                         @RequestParam String listName) {
        List<ListRecord> listRecords = selectConvertedList(partyId, shopId, listName, com.rbkmoney.fraudbusters.management.domain.enums.ListType.black);
        return ResponseEntity.ok().body(listRecords);
    }

    @PostMapping(value = "/greyList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<String>> insertRowToGrey(@RequestBody List<PaymentCountInfo> records) {
        return insertInList(this::insertGreyList, records);
    }

    @DeleteMapping(value = "/greyList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<String> removeRowFromGreyList(@RequestBody PaymentCountInfo record) {
        Row row = countInfoListRecordToRowConverter.convert(record);
        log.info("WbListResource greyList remove record {}", record);
        String idMessage = wbListCommandService.sendCommandSync(row, ListType.grey, Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/greyList")
    @PreAuthorize("hasAnyAuthority('fraud-officer')")
    public ResponseEntity<List<PaymentCountInfo>> getGreyList(@RequestParam(required = false) String partyId,
                                                              @RequestParam(required = false) String shopId,
                                                              @RequestParam String listName) {
        List<PaymentCountInfo> listRecords = wbListDao.getFilteredListRecords(partyId, shopId, com.rbkmoney.fraudbusters.management.domain.enums.ListType.grey, listName)
                .stream()
                .map(wbListRecordsToListRecordWithRowConverter::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(listRecords);
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
        return wbListCommandService.sendCommandSync(row, ListType.white, Command.CREATE);
    }

    private String insertGreyList(PaymentCountInfo record) {
        Row row = countInfoListRecordToRowConverter.convert(record);
        log.info("WbListResource greyList add record {}", record);
        return wbListCommandService.sendCommandSync(row, ListType.grey, Command.CREATE);
    }

    private List<ListRecord> selectConvertedList(String partyId, String shopId, String listName,
                                                 com.rbkmoney.fraudbusters.management.domain.enums.ListType type) {
        List<WbListRecords> filteredListRecords = wbListDao.getFilteredListRecords(partyId, shopId, type, listName);
        return filteredListRecords.stream()
                .map(wbListRecordsToListRecordConverter::destinationToSource)
                .collect(Collectors.toList());
    }

    private String insertBlackList(PaymentListRecord record) {
        Row row = paymentListRecordToRowConverter.convert(record);
        log.info("WbListResource blackList add record {}", record);
        return wbListCommandService.sendCommandSync(row, ListType.black, Command.CREATE);
    }

}
