package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fraudbusters.management.converter.p2p.P2pEventToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.p2p.wblist.P2PWbListDao;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.P2pWbListRecords;
import com.rbkmoney.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class P2pWbListEventListener {

    private final P2PWbListDao p2PWbListDao;
    private final P2pEventToListRecordConverter p2pEventToListRecordConverter;

    @KafkaListener(topics = "${kafka.topic.p2p.wblist.event.sink}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Event event) throws DaoException {
        log.info("WbListListener event: {}", event);
        P2pWbListRecords record = p2pEventToListRecordConverter.convert(event);
        switch (event.getEventType()) {
            case CREATED:
                p2PWbListDao.saveListRecord(record);
                break;
            case DELETED:
                p2PWbListDao.removeRecord(record);
                break;
            default:
                log.warn("WbListListener event for list not found! event: {}", event);
        }
    }
}
