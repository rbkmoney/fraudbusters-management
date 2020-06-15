package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.p2p.template.P2PTemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2PTemplateQueryResource {

    private final P2PTemplateDao templateDao;
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
        List<TemplateModel> list = templateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

}
