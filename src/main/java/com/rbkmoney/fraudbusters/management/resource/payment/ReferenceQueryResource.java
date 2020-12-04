package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.response.FilterPaymentReferenceResponse;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
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

    @GetMapping(value = "/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<PaymentReferenceModel>> getReferencesByFilters(Principal principal,
                                                                              @RequestParam(value = "partyId") String partyId,
                                                                              @RequestParam(value = "shopId") String shopId,
                                                                              @RequestParam(value = "isGlobal") Boolean isGlobal,
                                                                              @RequestParam(value = "isDefault") Boolean isDefault,
                                                                              @Validated @RequestParam(required = false) Integer limit) {
        log.info("TemplateManagementResource getReferences initiator: {} partyId: {} shopId: {} isGlobal: {} isDefault: {} limit: {}",
                principal.getName(), partyId, shopId, isGlobal, isDefault, limit);
        List<PaymentReferenceModel> listByTemplateId = referenceDao.getListByTFilters(partyId, shopId, isGlobal, isDefault, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    //todo isGlobal isDefault
    @GetMapping(value = "/reference/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterPaymentReferenceResponse> filterReferences(Principal principal,
                                                                           FilterRequest filterRequest,
                                                                           @Validated @RequestParam(required = false) Boolean isGlobal,
                                                                           @Validated @RequestParam(required = false) Boolean isDefault) {
        log.info("filterReferences initiator: {} filterRequest: {}, isGlobal: {}, isDefault: {}",
                principal.getName(), filterRequest, isGlobal, isDefault);
        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(filterRequest, isGlobal, isDefault);
        Integer count = referenceDao.countFilterModel(filterRequest.getSearchValue(), isGlobal, isDefault);
        return ResponseEntity.ok().body(FilterPaymentReferenceResponse.builder()
                .count(count)
                .referenceModels(paymentReferenceModels)
                .build());
    }

}
