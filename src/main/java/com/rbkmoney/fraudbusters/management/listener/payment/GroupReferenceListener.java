package com.rbkmoney.fraudbusters.management.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.payment.CommandToPaymentGroupReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.exception.UnknownReferenceException;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupReferenceListener extends CommandListener {

    private final PaymentGroupReferenceDao groupReferenceDao;
    private final CommandToPaymentGroupReferenceModelConverter groupReferenceModelConverter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.payment.group.reference}", containerFactory = "kafkaGroupReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("GroupReferenceListener command: {}", command);
        if (command.getCommandBody().isSetGroupReference()) {
            handle(command, groupReferenceModelConverter, groupReferenceDao::insert, groupReferenceDao::remove);
        } else {
            log.warn("Unknown reference in command in ReferenceListener! command: {}", command);
        }
    }
}
