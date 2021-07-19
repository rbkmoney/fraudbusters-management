package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordsModelToWbListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.NotFoundException;
import com.rbkmoney.fraudbusters.management.utils.PaymentCountInfoGenerator;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentCountInfo;
import com.rbkmoney.swag.fraudbusters.management.model.WbListRecordsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsListsService {

    private final WbListDao wbListDao;
    private final WbListCommandService wbListCommandService;
    private final WbListRecordToRowConverter wbListRecordToRowConverter;
    private final PaymentCountInfoGenerator paymentCountInfoGenerator;
    private final UserInfoService userInfoService;
    private final CsvPaymentCountInfoParser csvPaymentCountInfoParser;
    private final WbListRecordsModelToWbListRecordConverter wbListRecordsModelToWbListRecordConverter;

    public WbListRecordsResponse filterLists(List<String> listNames, String listType,
                                             FilterRequest filterRequest) {
        List<WbListRecords> wbListRecords =
                wbListDao.filterListRecords(ListType.valueOf(listType), listNames, filterRequest);
        Integer count =
                wbListDao.countFilterRecords(ListType.valueOf(listType), listNames, filterRequest.getSearchValue());
        return new WbListRecordsResponse()
                .count(count)
                .result(wbListRecords.stream()
                        .map(wbListRecordsModelToWbListRecordConverter::convert)
                        .collect(Collectors.toList()));
    }

    public void insertCsv(String listType, MultipartFile file, String initiator) {
        if (csvPaymentCountInfoParser.hasCsvFormat(file)) {
            try {
                List<PaymentCountInfo> paymentCountInfos = csvPaymentCountInfoParser.parse(file.getInputStream());
                log.info("Insert from csv paymentCountInfos size: {}", paymentCountInfos.size());
                wbListCommandService.sendListRecords(
                        paymentCountInfos,
                        com.rbkmoney.damsel.wb_list.ListType.valueOf(listType),
                        paymentCountInfoGenerator::initRow,
                        initiator);
                log.info("Insert loaded fraudPayments: {}", paymentCountInfos);
            } catch (IOException e) {
                log.error("Insert error when loadFraudOperation e: ", e);
                throw new RuntimeException(e);
            }
        }
    }

    public String removeListRecord(String id) {
        var wbListRecord = wbListDao.getById(id);
        if (wbListRecord == null) {
            log.error("List remove record not fount: {}", id);
            throw new NotFoundException(String.format("List record not found with id: %s", id));
        }
        log.info("removeRowFromList initiator: {} record {}", userInfoService.getUserName(), wbListRecord);
        var row = wbListRecordToRowConverter.convert(wbListRecord);
        return wbListCommandService.sendCommandSync(row,
                com.rbkmoney.damsel.wb_list.ListType.valueOf(wbListRecord.getListType().name()),
                Command.DELETE,
                userInfoService.getUserName());
    }

}
