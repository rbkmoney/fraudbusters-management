package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pWbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.p2p.request.P2pListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.p2p.response.P2pFilterListRecordsResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.P2pCountInfoGenerator;
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
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2pListsResource {

    private final P2PWbListDao wbListDao;
    private final WbListCommandService wbListCommandService;
    private final P2pWbListRecordToRowConverter wbListRecordToRowConverter;
    private final P2pCountInfoGenerator p2pCountInfoGenerator;

    @PostMapping(value = "/lists")
    @PreAuthorize("hasAnyAuthority('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> insertRowsToList(@Validated @RequestBody P2pListRowsInsertRequest request) {
        log.info("insertRowsToList request {}", request);
        return wbListCommandService.sendListRecords(request.getRecords(), request.getListType(), p2pCountInfoGenerator::initRow);
    }

    @DeleteMapping(value = "/lists/{id}")
    @PreAuthorize("hasAnyAuthority('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<String> removeRowFromList(@Validated @PathVariable String id) {
        P2pWbListRecords record = wbListDao.getById(id);
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

    @GetMapping(value = "/lists/filter")
    @PreAuthorize("hasAnyAuthority('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<P2pFilterListRecordsResponse> filterList(@Validated @RequestParam ListType listType,
                                                                   @Validated @RequestParam List<String> listNames,
                                                                   @Validated @RequestParam(required = false) String searchValue,
                                                                   @Validated @RequestParam(required = false) String lastId,
                                                                   @Validated @RequestParam(required = false) String sortFieldValue,
                                                                   @Validated @RequestParam(required = false) Integer size,
                                                                   @Validated @RequestParam(required = false) String sortBy,
                                                                   @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterList listType: {} listNames: {} searchValue: {} lastId: {} sortFieldValue: {} size: {} sortBy: {} sortOrder: {}",
                listType, listNames, searchValue, lastId, sortFieldValue, size, sortBy, sortOrder);
        List<P2pWbListRecords> wbListRecords = wbListDao.filterListRecords(listType, listNames, searchValue, lastId,
                sortFieldValue, size, sortBy, sortOrder);
        Integer count = wbListDao.countFilterRecords(listType, listNames, searchValue);
        return ResponseEntity.ok().body(P2pFilterListRecordsResponse.builder()
                .count(count)
                .wbListRecords(wbListRecords)
                .build());
    }

    @GetMapping(value = "/lists/names")
    @PreAuthorize("hasAnyAuthority('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getNames(@Validated @RequestParam ListType listType) {
        log.info("getNames listType: {}", listType);
        List<String> currentListNames = wbListDao.getCurrentListNames(listType);
        return ResponseEntity.ok().body(currentListNames);
    }

}
