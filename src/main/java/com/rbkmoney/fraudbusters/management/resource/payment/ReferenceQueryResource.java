package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.FilterPaymentReferenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReferenceQueryResource {

    private final PaymentReferenceDao referenceDao;

    @GetMapping(value = "/reference")
    public ResponseEntity<List<PaymentReferenceModel>> getReferencesByFilters(@RequestParam(value = "partyId") String partyId,
                                                                              @RequestParam(value = "shopId") String shopId,
                                                                              @RequestParam(value = "isGlobal") Boolean isGlobal,
                                                                              @RequestParam(value = "isDefault") Boolean isDefault,
                                                                              @Validated @RequestParam(required = false) Integer limit) {
        log.info("TemplateManagementResource getReferences partyId: {} shopId: {} isGlobal: {} isDefault: {} limit: {}", partyId, shopId, isGlobal, isDefault, limit);
        List<PaymentReferenceModel> listByTemplateId = referenceDao.getListByTFilters(partyId, shopId, isGlobal, isDefault, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/reference/filter")
    public ResponseEntity<FilterPaymentReferenceResponse> filterReferences(@Validated @RequestParam(required = false) String searchValue,
                                                                          @Validated @RequestParam(required = false) Boolean isGlobal,
                                                                          @Validated @RequestParam(required = false) Boolean isDefault,
                                                                          @Validated @RequestParam(required = false) String lastId,
                                                                          @Validated @RequestParam(required = false) Integer size,
                                                                          @Validated @RequestParam(required = false) String sortField,
                                                                          @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterReferences searchValue: {} lastId: {} size: {} sortOrder: {}, isGlobal: {}, isDefault: {}",
                searchValue, lastId, size, sortOrder, isGlobal, isDefault);
        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(searchValue, isGlobal,
                isDefault, lastId, size, sortField, sortOrder);
        Integer count = referenceDao.countFilterModel(searchValue, isGlobal, isDefault);
        return ResponseEntity.ok().body(FilterPaymentReferenceResponse.builder()
                .count(count)
                .referenceModels(paymentReferenceModels)
                .build());
    }

}
