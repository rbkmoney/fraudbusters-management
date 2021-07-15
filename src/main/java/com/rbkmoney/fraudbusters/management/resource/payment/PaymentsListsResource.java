package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.service.WbListCommandService;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.ParametersService;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsListsApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<WbListRecordsResponse> filterLists(@NotNull @Valid List<String> listNames,
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
        return ResponseEntity.ok(new WbListRecordsResponse()
                .count(count)
                .result(wbListRecords.stream().map(wbListRecord -> new WbListRecord()
                        .createdByUser(wbListRecord.getCreatedByUser())
                        .insertTime(wbListRecord.getInsertTime())
                        .listName(wbListRecord.getListName())
                        .listType(WbListRecord.ListTypeEnum.valueOf(wbListRecord.getListType().name()))
                        .partyId(wbListRecord.getPartyId())
                        .shopId(wbListRecord.getShopId())
                        .rowInfo(wbListRecord.getRowInfo())
                        .value(wbListRecord.getValue())).collect(Collectors.toList()))
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getAvailableListNames() {
        var listResponse = new ListResponse();
        listResponse.setResult(parametersService.getAvailableListNames());
        return ResponseEntity.ok().body(listResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> insertRow(@Valid RowListRequest request) {
        log.info("insertRowsToList initiator: {} request {}", userInfoService.getUserName(), request);
        return wbListCommandService.sendListRecords(
                request.getRecords(),
                com.rbkmoney.damsel.wb_list.ListType.valueOf(request.getListType().getValue()),
                paymentCountInfoGenerator::initRow,
                userInfoService.getUserName());
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getCurrentListNames(@NotNull @Valid String listType) {
        var listResponse = new ListResponse();
        listResponse.setResult(wbListDao.getCurrentListNames(ListType.valueOf(listType)));
        return ResponseEntity.ok().body(listResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<Void> insertFromCsv(@Valid String listType, @Valid MultipartFile file) {
        log.info("Insert from csv initiator: {} listType: {}", userInfoService.getUserName(), listType);
        if (csvPaymentCountInfoParser.hasCsvFormat(file)) {
            try {
                List<PaymentCountInfo> paymentCountInfos = csvPaymentCountInfoParser.parse(file.getInputStream());
                log.info("Insert from csv paymentCountInfos size: {}", paymentCountInfos.size());
                wbListCommandService.sendListRecords(
                        paymentCountInfos,
                        com.rbkmoney.damsel.wb_list.ListType.valueOf(listType),
                        paymentCountInfoGenerator::initRow,
                        userInfoService.getUserName());
                log.info("Insert loaded fraudPayments: {}", paymentCountInfos);
            } catch (IOException e) {
                log.error("Insert error when loadFraudOperation e: ", e);
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok().build();
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

}
