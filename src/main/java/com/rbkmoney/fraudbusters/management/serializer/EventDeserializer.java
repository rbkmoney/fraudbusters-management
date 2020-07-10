package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDeserializer extends AbstractThriftDeserializer<Event> {

    @Override
    public Event deserialize(String topic, byte[] data) {
        return deserialize(data, new Event());
    }
}