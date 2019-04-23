package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.damsel.wb_list.ChangeCommand;
import com.rbkmoney.deserializer.AbstractDeserializerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandChangeDeserializer extends AbstractDeserializerAdapter<ChangeCommand> {

    @Override
    public ChangeCommand deserialize(String topic, byte[] data) {
        return deserialize(data, new ChangeCommand());
    }
}