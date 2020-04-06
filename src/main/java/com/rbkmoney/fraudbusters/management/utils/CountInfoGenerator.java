package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CountInfoGenerator {

    private final ObjectMapper objectMapper;

    public CountInfo initCountInfo(String rowInfo) {
        CountInfo countInfoValue = new CountInfo();
        try {
            com.rbkmoney.damsel.wb_list.CountInfo countInfo = objectMapper.readValue(rowInfo, com.rbkmoney.damsel.wb_list.CountInfo.class);
            countInfoValue.setCount(countInfo.getCount());
            countInfoValue.setEndCountTime(countInfo.getTimeToLive());
            countInfoValue.setStartCountTime(countInfo.getStartCountTime());
        } catch (IOException e) {
            throw new RuntimeException("Error when read countInfo for rowInfo: " + rowInfo, e);
        }
        return countInfoValue;
    }

}
