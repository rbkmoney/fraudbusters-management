package com.rbkmoney.fraudbusters.management.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RowToWbListRecordsConverter implements Converter<Row, WbListRecords> {

    private final ObjectMapper objectMapper;

    @Override
    public WbListRecords convert(Row destination) {
        WbListRecords wbListRecords = new WbListRecords();
        wbListRecords.setPartyId(destination.getPartyId());
        wbListRecords.setShopId(destination.getShopId());
        wbListRecords.setListName(destination.getListName());
        wbListRecords.setListType(initListType(destination));
        wbListRecords.setRowInfo(initRowInfo(destination));
        return wbListRecords;
    }

    private ListType initListType(Row destination) {
        switch (destination.getListType()) {
            case grey:
                return ListType.grey;
            case black:
                return ListType.black;
            case white:
                return ListType.white;
            default:
                throw new RuntimeException("Unknown list type!");
        }
    }

    private String initRowInfo(Row destination) {
        if (destination.getRowInfo() != null && destination.getRowInfo().isSetCountInfo()) {
            try {
                return objectMapper.writeValueAsString(destination.getRowInfo().getCountInfo());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unknown list type!");
            }
        }
        return null;
    }
}
