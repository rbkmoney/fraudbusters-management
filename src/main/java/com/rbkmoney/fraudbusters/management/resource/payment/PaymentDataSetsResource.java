package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.converter.payment.DataSetToTestDataSetModelConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.TestDataSetModelToDataSetApiConverter;
import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsDataSetService;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsDataSetApi;
import com.rbkmoney.swag.fraudbusters.management.model.ApplyRuleOnHistoricalDataSetRequest;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import com.rbkmoney.swag.fraudbusters.management.model.DataSetsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentDataSetsResource implements PaymentsDataSetApi {

    private final UserInfoService userInfoService;
    private final PaymentsDataSetService paymentsDataSetService;
    private final TestDataSetModelToDataSetApiConverter testDataSetModelToDataSetApiConverter;
    private final DataSetToTestDataSetModelConverter dataSetToTestDataSetModelConverter;
    private final HistoricalDataServiceSrv.Iface historicalDataServiceSrv;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> applyRuleOnHistoricalDataSet(
            @Valid ApplyRuleOnHistoricalDataSetRequest applyRuleOnHistoricalDataSetRequest) {
        String userName = userInfoService.getUserName();
        log.info("applyRuleOnHistoricalDataSet initiator: {} applyRuleOnHistoricalDataSetRequest: {}", userName,
                applyRuleOnHistoricalDataSetRequest);
        var emulationRule = new EmulationRule();
        emulationRule.setTemplateEmulation(new OnlyTemplateEmulation()
                .setTemplate(new Template()
                        .setTemplate(applyRuleOnHistoricalDataSetRequest.getTemplate().getBytes())));
        try {
            var historicalDataSetCheckResult =
                    historicalDataServiceSrv.applyRuleOnHistoricalDataSet(new EmulationRuleApplyRequest()
                            .setEmulationRule(emulationRule)
                            .setTransactions(applyRuleOnHistoricalDataSetRequest.getRecords().stream()
                                    .map(payment -> new Payment())
                                    .collect(Collectors.toSet())));
            var historicalTransactionCheck =
                    historicalDataSetCheckResult.getHistoricalTransactionCheck();
            log.info("applyRuleOnHistoricalDataSet historicalTransactionCheck: {}", historicalTransactionCheck);
            return ResponseEntity.ok().body(historicalTransactionCheck.toString());
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<DataSetsResponse> filterDataSets(@Valid String continuationId, @Valid String sortOrder,
                                                           @Valid String sortBy, @Valid Integer size,
                                                           @Valid String from, @Valid String to,
                                                           @Valid String dataSetName) {
        var filterRequest = new FilterRequest(dataSetName, continuationId, null, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        String userName = userInfoService.getUserName();
        log.info("filterDataSets initiator: {} filterRequest: {}", userName, filterRequest);
        List<TestDataSetModel> testDataSetModels = paymentsDataSetService.filterDataSets(filterRequest);
        return ResponseEntity.ok(new DataSetsResponse()
                .continuationId(buildContinuationId(size, testDataSetModels))
                .result(testDataSetModels.stream()
                        .map(testDataSetModelToDataSetApiConverter::convert)
                        .collect(Collectors.toList())
                ));
    }

    private String buildContinuationId(Integer filterSize, List<TestDataSetModel> dataSetModels) {
        if (dataSetModels.size() == filterSize) {
            var lastDataSet = dataSetModels.get(dataSetModels.size() - 1);
            return lastDataSet.getId();
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<DataSet> getDataSet(String setId) {
        String userName = userInfoService.getUserName();
        log.info("insertDataSet initiator: {} id: {}", userName, setId);
        var dataSet = paymentsDataSetService.getDataSet(setId);
        log.info("getDataSet succeeded insertDataSet: {}", dataSet);
        return ResponseEntity.ok(testDataSetModelToDataSetApiConverter.convert(dataSet));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertDataSet(@Valid DataSet dataSet) {
        String userName = userInfoService.getUserName();
        log.info("insertDataSet initiator: {} dataSet: {}", userName, dataSet);
        Long id = paymentsDataSetService.insertDataSet(dataSetToTestDataSetModelConverter.convert(dataSet), userName);
        log.info("insertDataSet succeeded name: {}", dataSet);
        return ResponseEntity.ok(String.valueOf(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeDataSet(String id) {
        String userName = userInfoService.getUserName();
        log.info("removeDataSet initiator: {} id: {}", userName, id);
        paymentsDataSetService.removeDataSet(id, userName);
        log.info("removeDataSet succeeded id: {}", id);
        return ResponseEntity.ok(id);
    }

}
