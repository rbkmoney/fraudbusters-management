package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.p2p.DefaultP2pReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.p2p.reference.P2pReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.DefaultP2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/p2p")
@RequiredArgsConstructor
public class P2pReferenceQueryResource {

    private final P2pReferenceDao referenceDao;
    private final DefaultP2pReferenceDaoImpl defaultP2pReferenceDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/reference/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterResponse<P2pReferenceModel>> filterReferences(Principal principal,
                                                                              FilterRequest filterRequest,
                                                                              @RequestParam(value = "isGlobal")
                                                                                      Boolean isGlobal) {
        log.info("filterReferences initiator: {}  filterRequest: {} isGlobal: {}",
                userInfoService.getUserName(principal), filterRequest, isGlobal);
        List<P2pReferenceModel> paymentReferenceModels = referenceDao.filterReferences(filterRequest, isGlobal);
        Integer count = referenceDao.countFilterModel(filterRequest.getSearchValue(), isGlobal);
        return ResponseEntity.ok().body(FilterResponse.<P2pReferenceModel>builder()
                .count(count)
                .result(paymentReferenceModels)
                .build());
    }

    @GetMapping(value = "/reference/default/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterResponse<DefaultP2pReferenceModel>> filterDefaultReferences(
            Principal principal, FilterRequest filterRequest) {
        log.info("filterReferences initiator: {}  filterRequest: {}", userInfoService.getUserName(principal),
                filterRequest);
        List<DefaultP2pReferenceModel> paymentReferenceModels = defaultP2pReferenceDao.filterReferences(filterRequest);
        Integer count = defaultP2pReferenceDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterResponse.<DefaultP2pReferenceModel>builder()
                .count(count)
                .result(paymentReferenceModels)
                .build());
    }

}
