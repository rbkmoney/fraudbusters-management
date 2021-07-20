package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.fraudbusters.management.converter.CommonAuditInternalToCommonAuditConverter;
import com.rbkmoney.fraudbusters.management.dao.audit.CommandAuditDao;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.CommandAudit;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.model.FilterLogsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final CommandAuditDao commandAuditDao;
    private final CommonAuditInternalToCommonAuditConverter commonAuditInternalToCommonAuditConverter;

    public FilterLogsResponse filterRecords(List<String> commandTypes, List<String> objectTypes, String from,
                                            String to, FilterRequest filterRequest) {
        var fromDate = LocalDateTime.parse(from, DateTimeUtils.DATE_TIME_FORMATTER);
        var toDate = LocalDateTime.parse(to, DateTimeUtils.DATE_TIME_FORMATTER);
        List<CommandAudit> commandAudits = commandAuditDao.filterLog(fromDate, toDate, commandTypes,
                objectTypes, filterRequest);
        Integer count = commandAuditDao.countFilterRecords(fromDate, toDate, commandTypes,
                objectTypes, filterRequest);
        return new FilterLogsResponse()
                .count(count)
                .result(commonAuditInternalToCommonAuditConverter.convert(commandAudits));
    }

}
