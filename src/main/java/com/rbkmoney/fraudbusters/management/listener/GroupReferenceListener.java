package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.p2p.CommandToP2pGroupReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.CommandToPaymentGroupReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.group.P2pGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.dao.payment.group.PaymentGroupReferenceDao;
import com.rbkmoney.fraudbusters.management.exception.UnknownReferenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupReferenceListener extends CommandListener {

    private final PaymentGroupReferenceDao groupReferenceDao;
    private final P2pGroupReferenceDao p2pGroupReferenceDao;

    private final CommandToPaymentGroupReferenceModelConverter groupReferenceModelConverter;
    private final CommandToP2pGroupReferenceModelConverter commandToP2pGroupReferenceModelConverter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.group.reference}", containerFactory = "kafkaGroupReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("GroupReferenceListener command: {}", command);

        if (command.getCommandBody().isSetP2pGroupReference()) {
            handle(command, commandToP2pGroupReferenceModelConverter, p2pGroupReferenceDao::insert, p2pGroupReferenceDao::remove);
        } else if (command.getCommandBody().isSetGroupReference()) {
            handle(command, groupReferenceModelConverter, groupReferenceDao::insert, groupReferenceDao::remove);
        } else {
            log.error("Unknown reference in command in ReferenceListener! command: {}", command);
            throw new UnknownReferenceException();
        }
    }
}
