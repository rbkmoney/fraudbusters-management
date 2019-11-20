package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                                                                       @Validated @RequestParam(required = false) int limit) {
        log.info("TemplateManagementResource getReferences partyId: {} shopId: {} isGlobal: {} limit: {}", partyId, shopId, isGlobal, limit);
        List<PaymentReferenceModel> listByTemplateId = referenceDao.getListByTFilters(partyId, shopId, isGlobal, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

}
