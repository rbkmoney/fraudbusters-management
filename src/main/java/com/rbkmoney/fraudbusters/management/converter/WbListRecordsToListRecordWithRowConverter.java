package com.rbkmoney.fraudbusters.management.converter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.CountInfo;
import com.rbkmoney.fraudbusters.management.domain.CountInfoListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WbListRecordsToListRecordWithRowConverter implements Converter<WbListRecords, CountInfoListRecord> {

    private final ObjectMapper objectMapper;

    public CountInfoListRecord convert(WbListRecords destination) {
        CountInfoListRecord countInfoListRecord = new CountInfoListRecord();
        countInfoListRecord.setValue(destination.getValue());
        countInfoListRecord.setPartyId(destination.getPartyId());
        countInfoListRecord.setShopId(destination.getShopId());
        if (!StringUtil.isNullOrEmpty(destination.getRowInfo())) {
            try {
                CountInfo countInfo = objectMapper.readValue(destination.getRowInfo(), CountInfo.class);
                countInfoListRecord.setCount(countInfo.getCount());
                countInfoListRecord.setEndCountTime(countInfo.getTimeToLive());
                countInfoListRecord.setStartCountTime(countInfo.getStartCountTime());
            } catch (IOException e) {
                throw new RuntimeException("Error when read countInfo for destination: " + destination, e);
            }
        }
        return countInfoListRecord;
    }
}
