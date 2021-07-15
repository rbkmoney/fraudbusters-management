package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.swag.fraudbusters.management.model.WbListRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class WbListRecordsModelToWbListRecordConverter implements Converter<WbListRecords, WbListRecord> {

    @NonNull
    @Override
    public WbListRecord convert(WbListRecords wbListRecord) {
        return new WbListRecord()
                .createdByUser(wbListRecord.getCreatedByUser())
                .insertTime(wbListRecord.getInsertTime())
                .listName(wbListRecord.getListName())
                .listType(WbListRecord.ListTypeEnum.valueOf(wbListRecord.getListType().name()))
                .partyId(wbListRecord.getPartyId())
                .shopId(wbListRecord.getShopId())
                .rowInfo(wbListRecord.getRowInfo())
                .value(wbListRecord.getValue());
    }
}
