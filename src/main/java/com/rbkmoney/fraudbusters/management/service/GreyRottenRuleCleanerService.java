package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GreyRottenRuleCleanerService {

    @Value("${service.cleaner.fresh-period}")
    private Integer freshPeriod;

    private final WbListDao wbListDao;

    @Scheduled(cron = "${service.cleaner.cron}")
    void clean() {
        LocalDateTime thresholdRotDate = LocalDateTime.now().minusDays(freshPeriod);
        wbListDao.removeRottenRecords(thresholdRotDate);
    }
}
