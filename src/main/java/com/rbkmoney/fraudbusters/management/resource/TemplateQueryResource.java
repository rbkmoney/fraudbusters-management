package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class TemplateQueryResource {

    private final TemplateDao templateDao;
    private final PaymentReferenceDao referenceDao;

    @GetMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<PaymentReferenceModel>> getReferences(@PathVariable(value = "id") String id,
                                                              @Validated @RequestParam(required = false) Integer limit) {
        log.info("TemplateManagementResource getReferences id: {} limit: {}", id, limit);
        List<PaymentReferenceModel> listByTemplateId = referenceDao.getListByTemplateId(id, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/template")
    public ResponseEntity<List<TemplateModel>> getListTemplate(
            @Validated @RequestParam(required = false) Integer limit) {
        log.info("TemplateManagementResource getListTemplate limit: {}", limit);
        List<TemplateModel> list = templateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

}
