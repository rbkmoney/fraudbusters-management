package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.p2p.CommandToP2pReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.CommandToReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.p2p.P2pReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.exception.UnknownReferenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceListener extends CommandListener {

    private final ReferenceDao<PaymentReferenceModel> referenceDao;
    private final ReferenceDao<P2pReferenceModel> p2pReferenceDao;
    private final CommandToReferenceModelConverter paymentReferenceConverter;
    private final CommandToP2pReferenceModelConverter p2pReferenceModelConverter;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.reference}", containerFactory = "kafkaReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("ReferenceListener command: {}", command);

        if (command.getCommandBody().isSetP2pReference()) {
            handle(command, p2pReferenceModelConverter, p2pReferenceDao::insert, p2pReferenceDao::remove);
        } else if (command.getCommandBody().isSetReference()) {
            handle(command, paymentReferenceConverter, referenceDao::insert, referenceDao::remove);
        } else {
            log.error("Unknown reference in command in ReferenceListener! command: {}", command);
            throw new UnknownReferenceException();
        }
    }
}
