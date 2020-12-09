package com.rbkmoney.fraudbusters.management.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToTemplateModelConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.template.P2pTemplateDao;
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
public class P2PTemplateListener extends CommandListener {

    private final P2pTemplateDao p2pTemplateDao;
    private final CommandToTemplateModelConverter converter;
    private final AuditService auditService;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.p2p.template}", containerFactory = "kafkaP2PTemplateListenerContainerFactory")
    public void listen(Command command) {
        log.info("P2PTemplateDao event: {}", command);
        handle(command, converter, p2pTemplateDao::insert, p2pTemplateDao::remove);
        auditService.logCommand(command);
    }

}
