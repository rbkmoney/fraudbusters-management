package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.CountInfoListRequest;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CountInfoListRequestGenerator {

    private final ObjectMapper objectMapper;

    public CountInfoListRequest initDestination(String rowInfo, ListRecord listRecord) {
        CountInfoListRequest countInfoListRecord = new CountInfoListRequest();
        countInfoListRecord.setListRecord(listRecord);

        if (!StringUtil.isNullOrEmpty(rowInfo)) {
            try {
                CountInfo countInfo = objectMapper.readValue(rowInfo, CountInfo.class);
                countInfoListRecord.setCount(countInfo.getCount());
                countInfoListRecord.setEndCountTime(countInfo.getTimeToLive());
                countInfoListRecord.setStartCountTime(countInfo.getStartCountTime());
            } catch (IOException e) {
                throw new RuntimeException("Error when read countInfo for rowInfo: " + rowInfo, e);
            }
        }

        return countInfoListRecord;
    }

}
