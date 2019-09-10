package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.dao.DaoException;
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
public class GroupReferenceListener {

    private final GroupReferenceDao referenceDao;
    private final CommandToGroupReferenceModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.group.reference}", containerFactory = "kafkaGroupReferenceListenerContainerFactory")
    public void listen(Command command) throws DaoException {
        log.info("GroupReferenceListener command: {}", command);
        GroupReferenceModel groupReferenceModel = converter.convert(command);
        switch (command.getCommandType()) {
            case CREATE:
                referenceDao.insert(groupReferenceModel);
                break;
            case DELETE:
                referenceDao.remove(groupReferenceModel);
                break;
            default:
                log.warn("GroupReferenceListener CommandType not found! groupReferenceModel: {}", groupReferenceModel);
        }
    }
}
