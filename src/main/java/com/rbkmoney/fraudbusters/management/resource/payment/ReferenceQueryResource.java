package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.response.FilterDefaultPaymentReferenceResponse;
import com.rbkmoney.fraudbusters.management.domain.payment.response.FilterPaymentReferenceResponse;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReferenceQueryResource {

    private final PaymentReferenceDao referenceDao;
    private final DefaultPaymentReferenceDaoImpl defaultPaymentReferenceDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/reference/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterPaymentReferenceResponse> filterReferences(Principal principal, FilterRequest filterRequest) {
        log.info("filterReferences initiator: {} filterRequest: {}",
                userInfoService.getUserName(principal), filterRequest);
        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(filterRequest);
        Integer count = referenceDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterPaymentReferenceResponse.builder()
                .count(count)
                .referenceModels(paymentReferenceModels)
                .build());
    }

    @GetMapping(value = "/reference/default/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterDefaultPaymentReferenceResponse> filterDefaultReferences(Principal principal, FilterRequest filterRequest) {
        log.info("filterReferences initiator: {} filterRequest: {}",
                userInfoService.getUserName(principal), filterRequest);
        List<DefaultPaymentReferenceModel> paymentReferenceModels = defaultPaymentReferenceDao.filterReferences(filterRequest);
        Integer count = referenceDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterDefaultPaymentReferenceResponse.builder()
                .count(count)
                .referenceModels(paymentReferenceModels)
                .build());
    }

}
