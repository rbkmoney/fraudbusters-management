package com.rbkmoney.fraudbusters.management.converter;


import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RowToWbListRecordsConverter {

    WbListRecords destinationToSource(Row destination);
}
