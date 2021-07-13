package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CountInfoUtils {

    private final ObjectMapper objectMapper;

    public CountInfo initRowCountInfo(String rowInfo) {
        CountInfo countInfoValue = new CountInfo();
        try {
            com.rbkmoney.damsel.wb_list.CountInfo countInfo =
                    objectMapper.readValue(rowInfo, com.rbkmoney.damsel.wb_list.CountInfo.class);
            countInfoValue.setCount(countInfo.getCount());
            countInfoValue.setEndCountTime(countInfo.getTimeToLive());
            countInfoValue.setStartCountTime(countInfo.getStartCountTime());
        } catch (IOException e) {
            throw new RuntimeException("Error when read countInfo for rowInfo: " + rowInfo, e);
        }
        return countInfoValue;
    }

    public com.rbkmoney.swag.fraudbusters.management.model.CountInfo initExternalRowCountInfo(String rowInfo) {
        var countInfoValue = new com.rbkmoney.swag.fraudbusters.management.model.CountInfo();
        try {
            com.rbkmoney.damsel.wb_list.CountInfo countInfo =
                    objectMapper.readValue(rowInfo, com.rbkmoney.damsel.wb_list.CountInfo.class);
            countInfoValue.setCount(countInfo.getCount());
            countInfoValue.setEndCountTime(DateTimeUtils.toOffsetDateTime(countInfo.getTimeToLive()));
            countInfoValue.setStartCountTime(DateTimeUtils.toOffsetDateTime(countInfo.getStartCountTime()));
        } catch (IOException e) {
            throw new RuntimeException("Error when read countInfo for rowInfo: " + rowInfo, e);
        }
        return countInfoValue;
    }

    public void initRowCountInfo(CountInfo countInfo, Row row) {
        String startCountTime = StringUtil.isNullOrEmpty(countInfo.getStartCountTime())
                ? Instant.now().toString()
                : countInfo.getStartCountTime();
        row.setRowInfo(RowInfo.count_info(new com.rbkmoney.damsel.wb_list.CountInfo()
                .setCount(countInfo.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(countInfo.getEndCountTime())));
    }

    public void initRowCountInfo(com.rbkmoney.swag.fraudbusters.management.model.CountInfo countInfo, Row row) {
        String startCountTime =
                StringUtil.isNullOrEmpty(countInfo.getStartCountTime().format(DateTimeUtils.DATE_TIME_FORMATTER))
                        ? Instant.now().toString()
                        : countInfo.getStartCountTime().format(DateTimeUtils.DATE_TIME_FORMATTER);
        row.setRowInfo(RowInfo.count_info(new com.rbkmoney.damsel.wb_list.CountInfo()
                .setCount(countInfo.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(countInfo.getEndCountTime().format(DateTimeUtils.DATE_TIME_FORMATTER))));
    }

    public RowInfo initRowInfo(com.rbkmoney.swag.fraudbusters.management.model.CountInfo countInfo) {
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
