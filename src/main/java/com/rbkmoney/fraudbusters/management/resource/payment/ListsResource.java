package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.ParametersService;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ListsResource {

    private final WbListDao wbListDao;
    private final WbListCommandService wbListCommandService;
    private final WbListRecordToRowConverter wbListRecordToRowConverter;
    private final PaymentCountInfoGenerator paymentCountInfoGenerator;
    private final UserInfoService userInfoService;
    private final ParametersService parametersService;
    private final CsvPaymentCountInfoParser csvPaymentCountInfoParser;

    @PostMapping(value = "/lists")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> insertRowsToList(Principal principal,
                                                         @Validated @RequestBody ListRowsInsertRequest request) {
        log.info("insertRowsToList initiator: {} request {}", userInfoService.getUserName(principal), request);
        return wbListCommandService.sendListRecords(
                request.getRecords(),
                request.getListType(),
                paymentCountInfoGenerator::initRow,
                userInfoService.getUserName(principal));
    }

    @DeleteMapping(value = "/lists/{id}")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<String> removeRowFromList(Principal principal, @Validated @PathVariable String id) {
        WbListRecords record = wbListDao.getById(id);
        if (record == null) {
            log.error("List remove record not fount: {}", id);
            throw new NotFoundException(String.format("List record not found with id: %s", id));
        }
        log.info("removeRowFromList initiator: {} record {}", userInfoService.getUserName(principal), record);
        Row row = wbListRecordToRowConverter.convert(record);
        String idMessage = wbListCommandService.sendCommandSync(row,
                com.rbkmoney.damsel.wb_list.ListType.valueOf(record.getListType().name()),
                Command.DELETE,
                userInfoService.getUserName(principal));
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/lists/filter")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<FilterResponse<WbListRecords>> filterList(Principal principal,
                                                                    @Validated @RequestParam ListType listType,
                                                                    @Validated @RequestParam List<String> listNames,
                                                                    FilterRequest filterRequest) {
        log.info("filterList initiator: {} listType: {} listNames: {} filterRequest: {}",
                userInfoService.getUserName(principal), listType, listNames, filterRequest);
        List<WbListRecords> wbListRecords = wbListDao.filterListRecords(listType, listNames, filterRequest);
        Integer count = wbListDao.countFilterRecords(listType, listNames, filterRequest.getSearchValue());
        return ResponseEntity.ok().body(
                FilterResponse.<WbListRecords>builder()
                        .count(count)
                        .result(wbListRecords)
                        .build());
    }

    @GetMapping(value = "/lists/names")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getNames(Principal principal, @Validated @RequestParam ListType listType) {
        log.info("getNames initiator: {} listType: {}", userInfoService.getUserName(principal), listType);
        List<String> currentListNames = wbListDao.getCurrentListNames(listType);
        return ResponseEntity.ok().body(currentListNames);
    }

    @GetMapping(value = "/lists/availableListNames")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getAvailableListNames(Principal principal) {
        log.info("getAvailableListNames initiator: {}", userInfoService.getUserName(principal));
        return ResponseEntity.ok().body(parametersService.getAvailableListNames());
    }

    @PostMapping(value = "/lists/insertFromCsv/{listType}")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public void insertFromCsv(Principal principal, @RequestParam("file") MultipartFile file,
                              @Validated @PathVariable com.rbkmoney.damsel.wb_list.ListType listType)
            throws TException {
        log.info("Insert from csv initiator: {} listType: {}", userInfoService.getUserName(principal), listType);
        if (csvPaymentCountInfoParser.hasCsvFormat(file)) {
            try {
                List<PaymentCountInfo> paymentCountInfos = csvPaymentCountInfoParser.parse(file.getInputStream());
                log.info("Insert from csv paymentCountInfos size: {}", paymentCountInfos.size());
                wbListCommandService.sendListRecords(
                        paymentCountInfos,
                        listType,
                        paymentCountInfoGenerator::initRow,
                        userInfoService.getUserName(principal));
                log.info("Insert loaded fraudPayments: {}", paymentCountInfos);
            } catch (IOException e) {
                log.error("Insert error when loadFraudOperation e: ", e);
                throw new RuntimeException(e);
            }
        }
    }
}
