package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pWbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.p2p.request.P2pListRowsInsertRequest;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.P2pCountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.ParametersService;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    private final UserInfoService userInfoService;
    private final ParametersService parametersService;

    @PostMapping(value = "/lists")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> insertRowsToList(Principal principal,
                                                         @Validated @RequestBody P2pListRowsInsertRequest request) {
        log.info("insertRowsToList initiator: {} request {}", userInfoService.getUserName(principal), request);
        return wbListCommandService.sendListRecords(
                request.getRecords(),
                request.getListType(),
                p2pCountInfoGenerator::initRow,
                userInfoService.getUserName(principal));
    }

    @DeleteMapping(value = "/lists/{id}")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<String> removeRowFromList(Principal principal, @Validated @PathVariable String id) {
        P2pWbListRecords record = wbListDao.getById(id);
        if (record == null) {
            log.error("List remove record not fount: {}", id);
            throw new NotFoundException(String.format("List record not found with id: %s", id));
        }
        log.info("removeRowFromList initiator: {} record {}", userInfoService.getUserName(principal), record);
        Row row = wbListRecordToRowConverter.convert(record);
        String idMessage = wbListCommandService.sendCommandSync(
                row,
                com.rbkmoney.damsel.wb_list.ListType.valueOf(record.getListType().name()),
                Command.DELETE,
                userInfoService.getUserName(principal));
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/lists/filter")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<FilterResponse<P2pWbListRecords>> filterList(Principal principal,
                                                                       @Validated @RequestParam ListType listType,
                                                                       @Validated @RequestParam List<String> listNames,
                                                                       FilterRequest filterRequest) {
        log.info("filterList initiator: {} listType: {} listNames: {} filterRequest: {}",
                userInfoService.getUserName(principal), listType, listNames, filterRequest);
        List<P2pWbListRecords> wbListRecords = wbListDao.filterListRecords(listType, listNames, filterRequest);
        Integer count = wbListDao.countFilterRecords(listType, listNames, filterRequest.getSortFieldValue());
        return ResponseEntity.ok().body(FilterResponse.<P2pWbListRecords>builder()
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
}
