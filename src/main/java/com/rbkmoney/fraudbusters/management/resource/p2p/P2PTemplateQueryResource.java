package com.rbkmoney.fraudbusters.management.resource.p2p;

import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterTemplateResponse;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
public class P2PTemplateQueryResource {

    private final TemplateDao p2pTemplateDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/template/names")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> getTemplatesName(Principal principal,
                                                         @Validated @RequestParam(required = false) String regexpName) {
        log.info("getTemplatesName initiator: {} regexpName: {}", userInfoService.getUserName(principal), regexpName);
        List<String> list = p2pTemplateDao.getListNames(regexpName);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterTemplateResponse> filterTemplates(Principal principal, FilterRequest filterRequest) {
        log.info("filterTemplates initiator: {} filterRequest: {}", userInfoService.getUserName(principal), filterRequest);
        List<TemplateModel> templateModels = p2pTemplateDao.filterModel(filterRequest);
        Integer count = p2pTemplateDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterTemplateResponse.builder()
                .count(count)
                .templateModels(templateModels)
                .build());
    }
}
