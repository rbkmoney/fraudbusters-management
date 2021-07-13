package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.ParametersService;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsListsApi;
import com.rbkmoney.swag.fraudbusters.management.model.GroupsResponse;
import com.rbkmoney.swag.fraudbusters.management.model.ListResponse;
import com.rbkmoney.swag.fraudbusters.management.model.RowListRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsListsResource implements PaymentsListsApi {

    private final WbListDao wbListDao;
    private final WbListCommandService wbListCommandService;
    private final WbListRecordToRowConverter wbListRecordToRowConverter;
    private final PaymentCountInfoGenerator paymentCountInfoGenerator;
    private final UserInfoService userInfoService;
    private final ParametersService parametersService;
    private final CsvPaymentCountInfoParser csvPaymentCountInfoParser;

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<GroupsResponse> filterLists(@NotNull @Valid List<String> listNames,
                                                      @NotNull @Valid String listType, @Valid String lastId,
                                                      @Valid String sortOrder, @Valid String searchValue,
                                                      @Valid String sortBy, @Valid String sortFieldValue,
                                                      @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        log.info("filterList initiator: {} listType: {} listNames: {} filterRequest: {}",
                userInfoService.getUserName(), listType, listNames, filterRequest);
        List<WbListRecords> wbListRecords =
                wbListDao.filterListRecords(ListType.valueOf(listType), listNames, filterRequest);
        Integer count =
                wbListDao.countFilterRecords(ListType.valueOf(listType), listNames, filterRequest.getSearchValue());
        return ResponseEntity.ok().body(new GroupsResponse()
                        .result(wbListRecords)
                FilterResponse.<WbListRecords>builder()
                        .count(count)
                        .result(wbListRecords)
                        .build());
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getAvailableListNames() {
        ListResponse listResponse = new ListResponse();
        listResponse.setResult(parametersService.getAvailableListNames());
        return ResponseEntity.ok().body(listResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> insertRow(@Valid RowListRequest request) {
        log.info("insertRowsToList initiator: {} request {}", userInfoService.getUserName(), request);
        return wbListCommandService.sendListRecords(
                request.getRecords(),
                com.rbkmoney.damsel.wb_list.ListType.valueOf(request.getListType().name()),
                paymentCountInfoGenerator::initRow,
                userInfoService.getUserName());
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<String> removeRow(String id) {
        WbListRecords record = wbListDao.getById(id);
        if (record == null) {
            log.error("List remove record not fount: {}", id);
            throw new NotFoundException(String.format("List record not found with id: %s", id));
        }
        log.info("removeRowFromList initiator: {} record {}", userInfoService.getUserName(), record);
        Row row = wbListRecordToRowConverter.convert(record);
        String idMessage = wbListCommandService.sendCommandSync(row,
                com.rbkmoney.damsel.wb_list.ListType.valueOf(record.getListType().name()),
                Command.DELETE,
                userInfoService.getUserName());
        return ResponseEntity.ok().body(idMessage);
    }

    @GetMapping(value = "/lists/names")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getNames(Principal principal, @Validated @RequestParam ListType listType) {
        log.info("getNames initiator: {} listType: {}", userInfoService.getUserName(principal), listType);
        List<String> currentListNames = wbListDao.getCurrentListNames(listType);
        return ResponseEntity.ok().body(currentListNames);
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
