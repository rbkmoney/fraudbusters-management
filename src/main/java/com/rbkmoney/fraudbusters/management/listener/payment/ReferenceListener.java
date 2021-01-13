package com.rbkmoney.fraudbusters.management.listener.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.management.converter.payment.CommandToPaymentReferenceModelConverter;
import com.rbkmoney.fraudbusters.management.dao.ReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.listener.CommandListener;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceListener extends CommandListener {

    private final ReferenceDao<PaymentReferenceModel> referenceDao;
    private final CommandToPaymentReferenceModelConverter paymentReferenceConverter;
    private final AuditService auditService;

    @KafkaListener(topics = "${kafka.topic.fraudbusters.payment.reference}", containerFactory = "kafkaReferenceListenerContainerFactory")
    public void listen(Command command) {
        log.info("ReferenceListener command: {}", command);
        if (command.getCommandBody().isSetReference()) {
            handle(command, paymentReferenceConverter, referenceDao::insert, referenceDao::remove);
            auditService.logCommand(command);
        } else {
            log.warn("Unknown reference in command in ReferenceListener! command: {}", command);
        }
    }
}
