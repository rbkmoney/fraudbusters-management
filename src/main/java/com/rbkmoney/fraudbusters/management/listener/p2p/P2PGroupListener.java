package com.rbkmoney.fraudbusters.management.listener.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToGroupModelConverter;
import com.rbkmoney.fraudbusters.management.dao.group.GroupDao;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2PGroupListener extends CommandListener {

    private final GroupDao groupDao;
    private final CommandToGroupModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.group.p2p}", containerFactory = "kafkaGroupListenerContainerFactory")
    public void listen(Command command) {
        log.info("GroupListener event: {}", command);
        handle(command, converter, groupDao::insert, groupDao::remove);
    }

}
