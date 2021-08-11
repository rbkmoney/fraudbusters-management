package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.converter.payment.TemplateModelToTemplateConverter;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentEmulateService;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsDataSetApi;
import com.rbkmoney.swag.fraudbusters.management.model.ApplyRuleOnHistoricalDataSetRequest;
import com.rbkmoney.swag.fraudbusters.management.model.DataSet;
import com.rbkmoney.swag.fraudbusters.management.model.DataSetsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentDataSetsResource implements PaymentsDataSetApi {

    private final UserInfoService userInfoService;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> applyRuleOnHistoricalDataSet(
            @Valid ApplyRuleOnHistoricalDataSetRequest applyRuleOnHistoricalDataSetRequest) {
        return PaymentsDataSetApi.super.applyRuleOnHistoricalDataSet(applyRuleOnHistoricalDataSetRequest);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<DataSetsResponse> filterDataSets(@Valid String from, @Valid String to,
                                                           @Valid String dataSetName) {
        return PaymentsDataSetApi.super.filterDataSets(from, to, dataSetName);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<DataSet> getDataSet(String setId) {
        return PaymentsDataSetApi.super.getDataSet(setId);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertDataSet(@Valid DataSet dataSet) {
        return PaymentsDataSetApi.super.insertDataSet(dataSet);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeDataSet(String id) {
        return PaymentsDataSetApi.super.removeDataSet(id);
    }

}
