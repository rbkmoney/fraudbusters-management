package com.rbkmoney.fraudbusters.management.converter;


import com.rbkmoney.damsel.wb_list.CountInfo;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.fraudbusters.management.domain.CountInfoListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountInfoListRecordToRowConverter implements Converter<CountInfoListRecord, Row> {

    private final ListRecordToRowConverter listRecordToRowConverter;

    @Override
    public Row convert(CountInfoListRecord destination) {
        Row row = listRecordToRowConverter.destinationToSource(destination);
        row.setRowInfo(RowInfo.count_info(new CountInfo()
                .setCount(destination.getCount())
                .setStartCountTime(destination.getStartCountTime())
                .setTimeToLive(destination.getEndCountTime())));
        return row;
    }
}
