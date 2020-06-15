package com.rbkmoney.fraudbusters.management.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToTemplateModelConverter;
import com.rbkmoney.fraudbusters.management.dao.template.TemplateDao;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTemplateListener extends CommandListener {

    private final TemplateDao templateDao;
    private final CommandToTemplateModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.template.payment}", containerFactory = "kafkaTemplateListenerContainerFactory")
    public void listen(Command command) {
        log.info("TemplateListener event: {}", command);
        handle(command, converter, templateDao::insert, templateDao::remove);
    }

}
