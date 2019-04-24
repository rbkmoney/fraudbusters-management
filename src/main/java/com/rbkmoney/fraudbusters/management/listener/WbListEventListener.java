package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.converter.EventToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
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

    @KafkaListener(topics = "${kafka.topic.wblist.event.sink}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Event event) throws DaoException {
        log.info("WbListListener event: {}", event);
        WbListRecords record = eventToListRecordConverter.convert(event);
        switch (event.getEventType()) {
            case CREATED:
                wbListDao.saveListRecord(record);
                break;
            case DELETED:
                wbListDao.removeRecord(record);
                break;
            default:
                log.warn("WbListListener event for list not found! event: {}", event);
        }
    }
}
