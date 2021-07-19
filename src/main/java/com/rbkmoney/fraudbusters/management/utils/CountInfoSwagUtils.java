package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.swag.fraudbusters.management.model.CountInfo;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CountInfoSwagUtils {

    private final ObjectMapper objectMapper;

    public CountInfo initExternalRowCountInfo(String rowInfo) {
        var countInfoValue = new com.rbkmoney.swag.fraudbusters.management.model.CountInfo();
        try {
            var countInfo = objectMapper.readValue(rowInfo, com.rbkmoney.damsel.wb_list.CountInfo.class);
            countInfoValue.setCount(countInfo.getCount());
            countInfoValue.setEndCountTime(DateTimeUtils.toDate(countInfo.getTimeToLive()));
            countInfoValue.setStartCountTime(DateTimeUtils.toDate(countInfo.getStartCountTime()));
        } catch (IOException e) {
            throw new RuntimeException("Error when read countInfo for rowInfo: " + rowInfo, e);
        }
        return countInfoValue;
    }

    public RowInfo initRowInfo(CountInfo countInfo) {
        String startCountTime =
                StringUtil.isNullOrEmpty(countInfo.getStartCountTime().format(DateTimeUtils.DATE_TIME_FORMATTER))
                        ? Instant.now().toString()
                        : countInfo.getStartCountTime().format(DateTimeUtils.DATE_TIME_FORMATTER);
        return RowInfo.count_info(new com.rbkmoney.damsel.wb_list.CountInfo()
                .setCount(countInfo.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(countInfo.getEndCountTime().format(DateTimeUtils.DATE_TIME_FORMATTER)));
    }
}
