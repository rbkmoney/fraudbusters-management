package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.dao.audit.CommandAuditDao;
import com.rbkmoney.fraudbusters.management.domain.enums.ObjectType;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterResponse;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuditResource {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private final CommandAuditDao commandAuditDao;
    private final UserInfoService userInfoService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    @GetMapping(value = "/audit/filter")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<FilterResponse<CommandAudit>> filter(Principal principal,
                                                               @Validated @RequestParam String from,
                                                               @Validated @RequestParam String to,
                                                               @Validated @RequestParam List<String> commandTypes,
                                                               @Validated @RequestParam List<String> objectTypes,
                                                               FilterRequest filterRequest) {
        log.info("filter initiator: {} from: {} to: {} commandTypes: {} objectTypes: {} filterRequest: {}",
                userInfoService.getUserName(principal), from, to, commandTypes, objectTypes, filterRequest);
        LocalDateTime fromDate = toDate(from);
        List<CommandAudit> commandAudits = commandAuditDao.filterLog(fromDate,
                toDate(to),
                commandTypes,
                objectTypes,
                filterRequest);
        Integer count = commandAuditDao.countFilterRecords(fromDate,
                toDate(to),
                commandTypes,
                objectTypes,
                filterRequest);
        return ResponseEntity.ok().body(FilterResponse.<CommandAudit>builder()
                .count(count)
                .result(commandAudits)
                .build());
    }

    private LocalDateTime toDate(@RequestParam @Validated String to) {
        return LocalDateTime.parse(to, formatter);
    }

    @GetMapping(value = "/audit/commandTypes")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getCommanfTypes(Principal principal) {
        log.info("filter initiator: {}", userInfoService.getUserName(principal));
        return ResponseEntity.ok().body(Arrays.stream(CommandType.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/audit/objectTypes")
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<List<String>> getObjectTypes(Principal principal) {
        log.info("filter initiator: {}", userInfoService.getUserName(principal));
        return ResponseEntity.ok().body(Arrays.stream(ObjectType.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
    }

}
