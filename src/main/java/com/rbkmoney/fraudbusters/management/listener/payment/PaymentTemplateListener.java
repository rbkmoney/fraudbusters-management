package com.rbkmoney.fraudbusters.management.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.CommandToTemplateModelConverter;
import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTemplateListener extends CommandListener {

    private final TemplateDao paymentTemplateDao;
    private final CommandToTemplateModelConverter converter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.payment.template}", containerFactory = "kafkaTemplateListenerContainerFactory")
    public void listen(Command command) {
        log.info("PaymentTemplateListener event: {}", command);
        handle(command, converter, paymentTemplateDao::insert, paymentTemplateDao::remove);
    }

}
