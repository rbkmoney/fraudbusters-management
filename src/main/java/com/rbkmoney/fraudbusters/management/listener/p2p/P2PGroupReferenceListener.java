package com.rbkmoney.fraudbusters.management.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.p2p.CommandToP2pGroupReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PGroupReferenceListener extends CommandListener {

    private final P2pGroupReferenceDao p2pGroupReferenceDao;
    private final CommandToP2pGroupReferenceModelConverter commandToP2pGroupReferenceModelConverter;
    private final AuditService auditService;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.p2p.group.reference}", containerFactory = "kafkaP2PGroupReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("P2PGroupReferenceListener command: {}", command);
        if (command.getCommandBody().isSetP2pGroupReference()) {
            handle(command, commandToP2pGroupReferenceModelConverter, p2pGroupReferenceDao::insert, p2pGroupReferenceDao::remove);
            auditService.logCommand(command);
        } else {
            log.warn("Unknown reference in command in P2PGroupReferenceListener! command: {}", command);
        }
    }
}
