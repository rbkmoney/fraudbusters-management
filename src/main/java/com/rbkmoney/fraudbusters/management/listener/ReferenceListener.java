package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.ReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceListener extends CommandListener<ReferenceModel> {

    private final ReferenceDao referenceDao;
    private final CommandToReferenceModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.reference}", containerFactory = "kafkaReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("ReferenceListener command: {}", command);
        handle(command, converter, referenceDao::insert, referenceDao::remove);
    }
}
