package com.rbkmoney.fraudbusters.management.converter.p2p;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.P2pId;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RowToP2pWbListRecordsConverter implements Converter<Row, P2pWbListRecords> {

    private final ObjectMapper objectMapper;

    @Override
    public P2pWbListRecords convert(Row destination) {
        P2pWbListRecords wbListRecords = new P2pWbListRecords();

        if (destination.isSetId() && destination.getId().isSetP2pId()) {
            P2pId p2pId = destination.getId().getP2pId();
            wbListRecords.setIdentityId(p2pId.getIdentityId());

            wbListRecords.setListName(destination.getListName());
            wbListRecords.setListType(initListType(destination));
            wbListRecords.setValue(destination.getValue());
            wbListRecords.setRowInfo(initRowInfo(destination));

            return wbListRecords;
        }

        throw new UnknownEventException();
    }

    private ListType initListType(Row destination) {
        switch (destination.getListType()) {
            case grey:
                return ListType.grey;
            case black:
                return ListType.black;
            case white:
                return ListType.white;
            case naming:
                return ListType.naming;
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
