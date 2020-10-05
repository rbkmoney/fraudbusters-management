package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.template.PaymentTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentTemplateQueryResource {

    private final PaymentTemplateDao paymentTemplateDao;
    private final PaymentReferenceDao referenceDao;

    @GetMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<PaymentReferenceModel>> getReferences(@PathVariable(value = "id") String id,
                                                                     @Validated @RequestParam(required = false) Integer limit) {
        log.info("getReferences id: {} limit: {}", id, limit);
        List<PaymentReferenceModel> listByTemplateId = referenceDao.getListByTemplateId(id, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/template")
    public ResponseEntity<List<TemplateModel>> getListTemplate(
            @Validated @RequestParam(required = false) Integer limit) {
        log.info("getListTemplate limit: {}", limit);
        List<TemplateModel> list = paymentTemplateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/filter")
    public ResponseEntity<List<TemplateModel>> filterTemplates(@Validated @RequestParam(required = false) String id,
                                                               @Validated @RequestParam(required = false) String lastId,
                                                               @Validated @RequestParam(required = false) Integer size,
                                                               @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterTemplates id: {} lastId: {} size: {} sortOrder: {}", id, lastId, size, sortOrder);
        List<TemplateModel> list = paymentTemplateDao.filterModel(id, lastId, size, sortOrder);
        return ResponseEntity.ok().body(list);
    }

}
