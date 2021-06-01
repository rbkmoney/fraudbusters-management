package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.wb_list.CountInfo;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.fraudbusters.management.domain.enums.ListType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RowUtilsService {

    private final ObjectMapper objectMapper;

    public ListType initListType(Row destination) {
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

    public String initRowInfo(Row destination) {
        if (destination.getRowInfo() != null && destination.getRowInfo().isSetCountInfo()) {
            try {
                return objectMapper.writeValueAsString(destination.getRowInfo().getCountInfo());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unknown list type!");
            }
        }
        return null;
    }

    public LocalDateTime getTimeToLive(Row destination) {
        return Optional.ofNullable(destination)
                .map(Row::getRowInfo)
                .map(RowInfo::getCountInfo)
                .map(CountInfo::getTimeToLive)
                .map(Instant::parse)
                .map(instant -> LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
                .orElseGet(() -> null);
    }

}
