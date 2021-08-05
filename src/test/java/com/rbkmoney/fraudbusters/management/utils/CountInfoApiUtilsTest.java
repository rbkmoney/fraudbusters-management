package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.swag.fraudbusters.management.model.CountInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountInfoApiUtilsTest {

    public static final String START_TIME = "2021-08-05T07:43:13.416";
    public static final String END_TIME = "2021-09-05T07:43:16";

    CountInfoApiUtils countInfoApiUtils = new CountInfoApiUtils(new ObjectMapper());

    @Test
    void initExternalRowCountInfo() {
        CountInfo countInfo = countInfoApiUtils.initExternalRowCountInfo("{" +
                "            \"count\": 4," +
                "            \"timeToLive\": \"" + END_TIME + "\"," +
                "            \"startCountTime\": \"" + START_TIME + "\"" +
                "        }");

        assertEquals(LocalDateTime.parse(START_TIME), countInfo.getStartCountTime());
        assertEquals(LocalDateTime.parse(END_TIME), countInfo.getEndCountTime());
    }

    @Test
    void initRowInfo() {
        RowInfo rowInfo = countInfoApiUtils.initRowInfo(new CountInfo()
                .count(4L)
                .startCountTime(LocalDateTime.parse(START_TIME))
                .endCountTime(LocalDateTime.parse(END_TIME)));

        assertEquals(START_TIME, rowInfo.getCountInfo().getStartCountTime());
        assertEquals(END_TIME, rowInfo.getCountInfo().getTimeToLive());
    }
}
