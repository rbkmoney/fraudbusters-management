package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.reference.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceListener {

    private final ReferenceDao referenceDao;
    private final CommandToReferenceModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.reference}", containerFactory = "kafkaReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("ReferenceListener command: {}", command);
        ReferenceModel referenceModel = converter.convert(command);
        switch (command.getCommandType()) {
            case CREATE:
                referenceDao.insert(referenceModel);
                break;
            case DELETE:
                referenceDao.remove(referenceModel);
                break;
            default:
                log.warn("ReferenceListener CommandType not found! templateModel: {}", referenceModel);
        }
    }
}
