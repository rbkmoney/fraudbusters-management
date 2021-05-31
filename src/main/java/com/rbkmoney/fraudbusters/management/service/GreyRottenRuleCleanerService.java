package com.rbkmoney.fraudbusters.management.service;

import org.springframework.scheduling.annotation.Scheduled;

public class GreyRottenRuleCleanerService {

    @Scheduled(cron = "${service.cleaner.cron}")
    void flush(){
       // TODO implement it

    }
}
