package com.rbkmoney.fraudbusters.management.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToTemplateModelConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.template.P2PTemplateDao;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PTemplateListener extends CommandListener {

    private final P2PTemplateDao p2pTemplateDao;
    private final CommandToTemplateModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.p2p.template}", containerFactory = "kafkaTemplateListenerContainerFactory")
    public void listen(Command command) {
        log.info("P2PTemplateDao event: {}", command);
        handle(command, converter, p2pTemplateDao::insert, p2pTemplateDao::remove);
    }

}
