package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.utils.FilterRequestUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentTemplateQueryResource {

    private final TemplateDao paymentTemplateDao;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/template/names")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> getTemplatesName(Principal principal,
                                                         @Validated @RequestParam(required = false) String regexpName) {
        log.info("getTemplatesName initiator: {} regexpName: {}", userInfoService.getUserName(principal), regexpName);
        List<String> list = paymentTemplateDao.getListNames(regexpName);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterResponse<TemplateModel>> filterTemplates(Principal principal,
                                                                         FilterRequest filterRequest) {
        log.info("filterTemplates initiator: {} filterRequest: {}", userInfoService.getUserName(principal),
                filterRequest);
        FilterRequestUtils.prepareFilterRequest(filterRequest);
        List<TemplateModel> templateModels = paymentTemplateDao.filterModel(filterRequest);
        Integer count = paymentTemplateDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(
                FilterResponse.<TemplateModel>builder()
                        .count(count)
                        .result(templateModels)
                        .build());
    }

}
