package com.rbkmoney.fraudbusters.management.converter;


import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ListRecordToRowConverter {

    Row destinationToSource(ListRecord destination);
}
