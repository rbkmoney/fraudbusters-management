package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.wb_list.Command;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.converter.payment.WbListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreyRottenRuleCleanerService {

    @Value("${service.cleaner.fresh-period}")
    private Integer freshPeriod;

    private final WbListDao wbListDao;
    private final WbListCommandService wbListCommandService;
    private final WbListRecordToRowConverter wbListRecordToRowConverter;

    @Scheduled(cron = "${service.cleaner.cron}")
    void clean() {
        LocalDateTime thresholdRotDate = LocalDateTime.now().minusDays(freshPeriod);
        List<WbListRecords> rotRecords = wbListDao.getRottenRecords(thresholdRotDate);
        if (CollectionUtils.isEmpty(rotRecords)) {
            log.info("Records older than {} not found", thresholdRotDate);
            return;
        }
        rotRecords.forEach(record -> {
            Row row = wbListRecordToRowConverter.convert(record);
            wbListCommandService.sendCommandSync(
                    row,
                    com.rbkmoney.damsel.wb_list.ListType.valueOf(record.getListType().name()),
                    Command.DELETE,
                    ""); // TODO инициатор нужен?
        }
        );
        wbListDao.removeRottenRecords(thresholdRotDate); // TODO а нужно ли оно?
    }
}
