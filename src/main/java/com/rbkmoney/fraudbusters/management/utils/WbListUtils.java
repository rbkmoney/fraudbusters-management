package com.rbkmoney.fraudbusters.management.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WbListUtils {

    public <T> ResponseEntity<List<String>> insertInList(BiFunction<T, String, String> func, List<T> records,
                                                         String initiator) {
        try {
            List<String> recordIds = records.stream()
                    .map(t -> func.apply(t, initiator))
                    .collect(Collectors.toList());
            return ResponseEntity.ok()
                    .body(recordIds);
        } catch (Exception e) {
            log.error("Error when insert rows: {} e: ", records, e);
            return ResponseEntity.status(500).build();
        }
    }

}
