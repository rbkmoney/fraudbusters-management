package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.response.FilterPaymentReferenceResponse;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReferenceQueryResource {

    private final PaymentReferenceDao referenceDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/reference/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterPaymentReferenceResponse> filterReferences(Principal principal,
                                                                           FilterRequest filterRequest,
                                                                           @Validated @RequestParam(required = false) Boolean isGlobal,
                                                                           @Validated @RequestParam(required = false) Boolean isDefault) {
        log.info("filterReferences initiator: {} filterRequest: {}, isGlobal: {}, isDefault: {}",
                userInfoService.getUserName(principal), filterRequest, isGlobal, isDefault);
        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(filterRequest, isGlobal, isDefault);
        Integer count = referenceDao.countFilterModel(filterRequest.getSearchValue(), isGlobal, isDefault);
        return ResponseEntity.ok().body(FilterPaymentReferenceResponse.builder()
                .count(count)
                .referenceModels(paymentReferenceModels)
                .build());
    }

}
