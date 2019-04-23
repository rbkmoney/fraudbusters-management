package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.fraudbusters.management.dao.reference.ReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TemplateQueryResource {

    private final TemplateDao templateDao;
    private final ReferenceDao referenceDao;

    @GetMapping(value = "/template/{id}/reference")
    public ResponseEntity<List<ReferenceModel>> getReferences(@RequestParam String id,
                                                              @Validated @RequestParam(required = false) int limit) {
        log.info("TemplateManagementResource getReferences id: {} limit: {}", id, limit);
        List<ReferenceModel> listByTemplateId = referenceDao.getListByTemplateId(id, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/template")
    public ResponseEntity<List<TemplateModel>> getListTemplate(
            @Validated @RequestParam(required = false) int limit) {
        log.info("TemplateManagementResource getListTemplate limit: {}", limit);
        List<TemplateModel> list = templateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

}
