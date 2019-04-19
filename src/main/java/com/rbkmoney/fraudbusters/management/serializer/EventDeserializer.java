package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.deserializer.AbstractDeserializerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDeserializer extends AbstractDeserializerAdapter<Event> {

    @Override
    public Event deserialize(String topic, byte[] data) {
        Event event = new Event();
        return deserialize(data, event);
    }
}