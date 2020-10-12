package com.rbkmoney.fraudbusters.management.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.p2p.CommandToP2pReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PReferenceListener extends CommandListener {

    private final ReferenceDao<P2pReferenceModel> p2pReferenceDao;
    private final CommandToP2pReferenceModelConverter p2pReferenceModelConverter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.p2p.reference}", containerFactory = "kafkaP2PReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("P2PReferenceListener command: {}", command);
        if (command.getCommandBody().isSetP2pReference()) {
            handle(command, p2pReferenceModelConverter, p2pReferenceDao::insert, p2pReferenceDao::remove);
        } else {
            log.warn("Unknown reference in command in P2PReferenceListener! command: {}", command);
        }
    }
}
