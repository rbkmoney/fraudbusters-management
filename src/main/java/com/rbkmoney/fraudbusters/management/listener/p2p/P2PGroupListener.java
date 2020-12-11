package com.rbkmoney.fraudbusters.management.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToGroupModelConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2PGroupDao;
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
public class P2PGroupListener extends CommandListener {

    private final P2PGroupDao p2pGroupDao;
    private final CommandToGroupModelConverter converter;
    private final AuditService auditService;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.p2p.group.list}", containerFactory = "kafkaP2PGroupListenerContainerFactory")
    public void listen(Command command) {
        log.info("GroupListener event: {}", command);
        handle(command, converter, p2pGroupDao::insert, p2pGroupDao::remove);
        auditService.logCommand(command);
    }

}
