package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.FilterTemplateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<P2pReferenceModel>> getReferences(@PathVariable(value = "id") String id,
                                                                 @Validated @RequestParam(required = false) Integer limit) {
        log.info("P2PTemplateQueryResource getReferences id: {} limit: {}", id, limit);
        List<P2pReferenceModel> listByTemplateId = p2pReferenceDao.getListByTemplateId(id, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/template")
    public ResponseEntity<List<TemplateModel>> getListTemplate(
            @Validated @RequestParam(required = false) Integer limit) {
        log.info("P2PTemplateQueryResource getListTemplate limit: {}", limit);
        List<TemplateModel> list = p2pTemplateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/names")
    public ResponseEntity<List<String>> getTemplatesName(@Validated @RequestParam(required = false) String regexpName) {
        log.info("getTemplatesName regexpName: {}", regexpName);
        List<String> list = p2pTemplateDao.getListNames(regexpName);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/filter")
    public ResponseEntity<FilterTemplateResponse> filterTemplates(@Validated @RequestParam(required = false) String id,
                                                                  @Validated @RequestParam(required = false) String lastId,
                                                                  @Validated @RequestParam(required = false) Integer size,
                                                                  @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterTemplates id: {} lastId: {} size: {} sortOrder: {}", id, lastId, size, sortOrder);
        List<TemplateModel> templateModels = p2pTemplateDao.filterModel(id, lastId, size, sortOrder);
        Integer count = p2pTemplateDao.countFilterModel(id);
        return ResponseEntity.ok().body(FilterTemplateResponse.builder()
                .count(count)
                .templateModels(templateModels)
                .build());
    }
}
