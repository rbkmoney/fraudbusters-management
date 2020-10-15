package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.response.FilterP2pReferenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2pReferenceQueryResource {

    private final P2pReferenceDao referenceDao;

    @GetMapping(value = "/reference")
    public ResponseEntity<List<P2pReferenceModel>> getReferencesByFilters(@RequestParam(value = "identityId") String identityId,
                                                                          @RequestParam(value = "isGlobal") Boolean isGlobal,
                                                                          @Validated @RequestParam(required = false) Integer limit) {
        log.info("P2pReferenceQueryResource getReferences partyId: {} isGlobal: {} limit: {}", identityId, isGlobal, limit);
        List<P2pReferenceModel> listByTemplateId = referenceDao.getListByTFilters(identityId, isGlobal, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/reference/filter")
    public ResponseEntity<FilterP2pReferenceResponse> filterReferences(@Validated @RequestParam(required = false) String searchValue,
                                                                      @Validated @RequestParam(required = false) Boolean isGlobal,
                                                                      @Validated @RequestParam(required = false) String lastId,
                                                                      @Validated @RequestParam(required = false) Integer size,
                                                                      @Validated @RequestParam(required = false) String sortField,
                                                                      @Validated @RequestParam(required = false) SortOrder sortOrder) {
        log.info("filterReferences searchValue: {} lastId: {} size: {} sortOrder: {}, isGlobal: {}",
                searchValue, lastId, size, sortOrder, isGlobal);
        List<P2pReferenceModel> paymentReferenceModels = referenceDao.filterReferences(searchValue, isGlobal, lastId, size, sortField, sortOrder);
        Integer count = referenceDao.countFilterModel(searchValue, isGlobal);
        return ResponseEntity.ok().body(FilterP2pReferenceResponse.builder()
                .count(count)
                .referenceModels(paymentReferenceModels)
                .build());
    }

}
