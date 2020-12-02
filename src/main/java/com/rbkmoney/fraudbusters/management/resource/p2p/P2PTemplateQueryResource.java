package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterTemplateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2PTemplateQueryResource {

    private final TemplateDao p2pTemplateDao;
    private final P2pReferenceDao p2pReferenceDao;

    @GetMapping(value = "/template/{id}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<P2pReferenceModel>> getReferences(@PathVariable(value = "id") String id,
                                                                 @Validated @RequestParam(required = false) Integer limit) {
        log.info("P2PTemplateQueryResource getReferences id: {} limit: {}", id, limit);
        List<P2pReferenceModel> listByTemplateId = p2pReferenceDao.getListByTemplateId(id, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/template")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<TemplateModel>> getListTemplate(
            @Validated @RequestParam(required = false) Integer limit) {
        log.info("P2PTemplateQueryResource getListTemplate limit: {}", limit);
        List<TemplateModel> list = p2pTemplateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/names")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> getTemplatesName(@Validated @RequestParam(required = false) String regexpName) {
        log.info("getTemplatesName regexpName: {}", regexpName);
        List<String> list = p2pTemplateDao.getListNames(regexpName);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterTemplateResponse> filterTemplates(FilterRequest filterRequest) {
        log.info("filterTemplates filterRequest: {}", filterRequest);
        List<TemplateModel> templateModels = p2pTemplateDao.filterModel(filterRequest);
        Integer count = p2pTemplateDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterTemplateResponse.builder()
                .count(count)
                .templateModels(templateModels)
                .build());
    }
}
