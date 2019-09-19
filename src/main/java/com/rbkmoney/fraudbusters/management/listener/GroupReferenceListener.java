package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToGroupReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.group.GroupReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.GroupReferenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupReferenceListener extends CommandListener<GroupReferenceModel> {

    private final GroupReferenceDao groupReferenceDao;
    private final CommandToGroupReferenceModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.group.reference}", containerFactory = "kafkaGroupReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("GroupReferenceListener command: {}", command);
        handle(command, converter, groupReferenceDao::insert, groupReferenceDao::remove);
    }
}
