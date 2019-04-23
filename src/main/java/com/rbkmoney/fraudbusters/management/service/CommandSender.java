package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.exception.KafkaProduceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandSender {

    private final KafkaTemplate kafkaTemplate;

    public String send(String topicName, Command command, String key) {
        try {
            kafkaTemplate.send(topicName, key, command).get();
        } catch (InterruptedException e) {
            log.error("InterruptedException command: {} e: ", command, e);
            Thread.currentThread().interrupt();
            throw new KafkaProduceException(e);
        } catch (Exception e) {
            log.error("Error when send command: {} e: ", command, e);
            throw new KafkaProduceException(e);
        }
        return key;
    }

}
