package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.payment.response.PaymentFilterListRecordsResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ListsResource {

    private final WbListDao wbListDao;
    private final WbListCommandService wbListCommandService;
    private final WbListRecordToRowConverter wbListRecordToRowConverter;
    private final PaymentCountInfoGenerator paymentCountInfoGenerator;

    @PostMapping(value = "/lists")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> insertRowsToList(@Validated @RequestBody ListRowsInsertRequest request) {
        log.info("insertRowsToList request {}", request);
        return wbListCommandService.sendListRecords(request.getRecords(), request.getListType(), paymentCountInfoGenerator::initRow);
    }

    @DeleteMapping(value = "/lists/{id}")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<String> removeRowFromList(@Validated @PathVariable String id) {
        WbListRecords record = wbListDao.getById(id);
        if (record == null) {
            log.error("List remove record not fount: {}", id);
            throw new NotFoundException(String.format("List record not found with id: %s", id));
        }
        log.info("removeRowFromList record {}", record);
        Row row = wbListRecordToRowConverter.convert(record);
        String idMessage = wbListCommandService.sendCommandSync(row,
                com.rbkmoney.damsel.wb_list.ListType.valueOf(record.getListType().name()), Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    //Мне кажется стоит вынести в отдельный объект, во многих местах такие параметры
    @GetMapping(value = "/lists/filter")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<PaymentFilterListRecordsResponse> filterList(@Validated @RequestParam ListType listType,
                                                                       @Validated @RequestParam List<String> listNames,
                                                                       @Validated @RequestParam(required = false) String searchValue,
                                                                       @Validated @RequestParam(required = false) String lastId,
                                                                       @Validated @RequestParam(required = false) String sortFieldValue,
                                                                       @Validated @RequestParam(required = false) Integer size,
                                                                       @Validated @RequestParam(required = false) String sortBy,
                                                                       @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterList listType: {} listNames: {} searchValue: {} lastId: {} sortFieldValue: {} size: {} sortBy: {} sortOrder: {}",
                listType, listNames, searchValue, lastId, sortFieldValue, size, sortBy, sortOrder);
        List<WbListRecords> wbListRecords = wbListDao.filterListRecords(listType, listNames, searchValue, lastId,
                sortFieldValue, size, sortBy, sortOrder);
        Integer count = wbListDao.countFilterRecords(listType, listNames, searchValue);
        return ResponseEntity.ok().body(PaymentFilterListRecordsResponse.builder()
                .count(count)
                .wbListRecords(wbListRecords)
                .build());
    }

    @GetMapping(value = "/lists/names")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getNames(@Validated @RequestParam ListType listType) {
        log.info("getNames listType: {}", listType);
        List<String> currentListNames = wbListDao.getCurrentListNames(listType);
        return ResponseEntity.ok().body(currentListNames);
    }

}
