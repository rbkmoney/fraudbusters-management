package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.damsel.wb_list.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.thrift.TDeserializer;

import java.util.Map;

@Slf4j
public class EventDeserializer implements Deserializer<Event> {

    private final ThreadLocal<TDeserializer> thriftDeserializer = ThreadLocal.withInitial(TDeserializer::new);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Event deserialize(String topic, byte[] data) {
        Event command = new Event();
        try {
            thriftDeserializer.get().deserialize(command, data);
        } catch (Exception e) {
            log.error("Error when deserialize command data: {} ", data, e);
            throw new RuntimeException();
        }
        return command;
    }

    @Override
    public void close() {

    }
}