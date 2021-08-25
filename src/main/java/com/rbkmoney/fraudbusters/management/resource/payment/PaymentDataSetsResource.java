package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.fraudbusters.management.converter.payment.*;
import com.rbkmoney.fraudbusters.management.domain.payment.TestCheckedDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.payment.TestDataSetModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsDataSetService;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsDataSetApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
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
    private final TestCheckedDataSetModelToCheckedDataSetApiConverter checkedDataSetModelToCheckedDataSetApiConverter;
    private final DataSetToTestDataSetModelConverter dataSetToTestDataSetModelConverter;
    private final HistoricalDataServiceSrv.Iface historicalDataServiceSrv;
    private final ApplyRuleOnHistoricalRequestToEmulationRuleApplyRequestConverter applyConverter;
    private final HistoricalDataSetCheckResultToTestCheckedDataSetModelConverter dataSetCheckResultToDaoModelConverter;

    @Override
    public ResponseEntity<CheckedDataSet> getCheckedDataSet(String id) {
        String userName = userInfoService.getUserName();
        log.info("getCheckedDataSet initiator: {} id: {}", userName, id);
        var dataSet = paymentsDataSetService.getCheckedDataSet(id);
        log.info("getCheckedDataSet succeeded checkedDataSet: {}", dataSet);
        return ResponseEntity.ok(checkedDataSetModelToCheckedDataSetApiConverter.convert(dataSet));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> applyRuleOnHistoricalDataSet(
            @Valid ApplyRuleOnHistoricalDataSetRequest request) {
        String userName = userInfoService.getUserName();
        log.info("applyRuleOnHistoricalDataSet initiator: {} request: {}", userName, request);
        try {
            var historicalDataSetCheckResult = historicalDataServiceSrv
                    .applyRuleOnHistoricalDataSet(applyConverter.convert(request));
            TestCheckedDataSetModel dataSetModel =
                    createCheckedDataSet(request, userName, historicalDataSetCheckResult);
            Long resultId = paymentsDataSetService.insertCheckedDataSet(dataSetModel, userName);
            log.info("applyRuleOnHistoricalDataSet resultId: {}", resultId);
            return ResponseEntity.ok().body(String.valueOf(resultId));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    private TestCheckedDataSetModel createCheckedDataSet(ApplyRuleOnHistoricalDataSetRequest request,
                                                         String userName,
                                                         com.rbkmoney.damsel.fraudbusters.HistoricalDataSetCheckResult
                                                                 historicalDataSetCheckResult) {
        TestCheckedDataSetModel dataSetModel =
                dataSetCheckResultToDaoModelConverter.convert(historicalDataSetCheckResult);
        dataSetModel.setInitiator(userName);
        PaymentReference reference = request.getReference();
        if (reference != null) {
            dataSetModel.setPartyId(reference.getPartyId());
            dataSetModel.setShopId(reference.getShopId());
        }
        dataSetModel.setTestDataSetId(request.getDataSetId());
        dataSetModel.setTemplate(request.getTemplate());
        return dataSetModel;
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
        List<TestDataSetModel> testDataSetModels = paymentsDataSetService.filterDataSets(from, to, filterRequest);
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
        log.info("getDataSet initiator: {} id: {}", userName, setId);
        var dataSet = paymentsDataSetService.getDataSet(setId);
        log.info("getDataSet succeeded dataSet: {}", dataSet);
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
