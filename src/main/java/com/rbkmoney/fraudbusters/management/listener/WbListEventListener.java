package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.converter.payment.EventToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.CdDao;
import com.rbkmoney.fraudbusters.management.dao.payment.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import com.rbkmoney.fraudbusters.management.exception.UnknownEventException;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WbListEventListener {

    private final WbListDao wbListDao;
    private final EventToListRecordConverter eventToListRecordConverter;
    private final AuditService auditService;

    @KafkaListener(topics = "${kafka.topic.wblist.event.sink}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Event event) throws DaoException {
        log.info("WbListListener event: {}", event);
        if (!event.getRow().isSetId() || event.getRow().getId().isSetPaymentId()) {
            WbListRecords record = eventToListRecordConverter.convert(event);
            applyCommand(event, record, wbListDao);
            auditService.logEvent(event);
        } else {
            log.error("Unknown event when wbListEventListener listen event: {}", event);
            throw new UnknownEventException(
                    String.format("Unknown event when wbListEventListener listen event: %s", event));
        }
    }

    private <T> void applyCommand(Event event, T record, CdDao<T> cdDao) {
        switch (event.getEventType()) {
            case CREATED -> cdDao.saveListRecord(record);
            case DELETED -> cdDao.removeRecord(record);
            default -> log.warn("WbListListener event for list not found! event: {}", event);
        }
    }

}
