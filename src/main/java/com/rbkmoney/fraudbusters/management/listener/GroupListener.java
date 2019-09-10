package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.converter.CommandToGroupModelConverter;
import com.rbkmoney.fraudbusters.management.dao.group.GroupDao;
import com.rbkmoney.fraudbusters.management.domain.GroupModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupListener {

    private final GroupDao groupDao;
    private final CommandToGroupModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.group.list}", containerFactory = "kafkaGroupListenerContainerFactory")
    public void listen(Command command) throws DaoException {
        log.info("GroupListener event: {}", command);
        GroupModel groupModel = converter.convert(command);
        switch (command.getCommandType()) {
            case CREATE:
                groupDao.insert(groupModel);
                break;
            case DELETE:
                groupDao.remove(groupModel);
                break;
            default:
                log.warn("GroupListener CommandType not found! groupModel: {}", groupModel);
        }
    }
}
