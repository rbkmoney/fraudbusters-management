package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.converter.CommandToTemplateModelConverter;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateListener {

    private final TemplateDao templateDao;
    private final CommandToTemplateModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.template}", containerFactory = "kafkaTemplateListenerContainerFactory")
    public void listen(Command command) throws DaoException {
        log.info("TemplateListener event: {}", command);
        TemplateModel templateModel = converter.convert(command);
        switch (command.getCommandType()) {
            case CREATE:
                templateDao.insert(templateModel);
                break;
            case DELETE:
                templateDao.remove(templateModel);
                break;
            default:
                log.warn("TemplateListener CommandType not found! templateModel: {}", templateModel);
        }
    }
}
