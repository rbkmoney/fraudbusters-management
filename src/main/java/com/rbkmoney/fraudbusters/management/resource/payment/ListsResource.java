package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.request.ListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterListRecordsResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/lists")
    public ResponseEntity<List<String>> insertRowsToList(@Validated @RequestBody ListRowsInsertRequest request) {
        return wbListCommandService.sendListRecords(request.getRecords(), request.getListType());
    }

    @DeleteMapping(value = "/lists/{id}")
    public ResponseEntity<String> removeRowFromList(@Validated @PathVariable String id) {
        WbListRecords record = wbListDao.getById(id);
        if (record == null) {
            log.error("List remove record not fount: {}", id);
            throw new NotFoundException(String.format("List record not found with id: %s", id));
        }
        log.info("WbListResource whiteList remove record {}", record);
        Row row = wbListRecordToRowConverter.convert(record);
        String idMessage = wbListCommandService.sendCommandSync(row,
                com.rbkmoney.damsel.wb_list.ListType.valueOf(record.getListType().getName()), Command.DELETE);
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/lists/filter")
    public ResponseEntity<FilterListRecordsResponse> filterList(@Validated @RequestParam(required = false) ListType listType,
                                                                @Validated @RequestParam(required = false) List<String> listNames,
                                                                @Validated @RequestParam(required = false) String idRegexp,
                                                                @Validated @RequestParam(required = false) String lastId,
                                                                @Validated @RequestParam(required = false) String sortFieldValue,
                                                                @Validated @RequestParam(required = false) Integer size,
                                                                @Validated @RequestParam(required = false) String sortBy,
                                                                @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterReference idRegexp: {}", idRegexp);
        List<WbListRecords> wbListRecords = wbListDao.filterListRecords(listType, listNames, idRegexp, lastId,
                sortFieldValue, size, sortBy, sortOrder);
        Integer count = wbListDao.countFilterRecords(listType, listNames, idRegexp);
        return ResponseEntity.ok().body(FilterListRecordsResponse.builder()
                .count(count)
                .wbListRecords(wbListRecords)
                .build());
    }

    @GetMapping(value = "/lists/names")
    public ResponseEntity<List<String>> getNames(@Validated @RequestParam(required = false) ListType listType) {
        log.info("filterList listType: {}", listType);
        List<String> currentListNames = wbListDao.getCurrentListNames(listType);
        return ResponseEntity.ok().body(currentListNames);
    }

}
