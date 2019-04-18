package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.damsel.wb_list.ChangeCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.thrift.TDeserializer;

import java.util.Map;

@Slf4j
public class CommandDeserializer implements Deserializer<ChangeCommand> {

    private final ThreadLocal<TDeserializer> thriftDeserializer = ThreadLocal.withInitial(TDeserializer::new);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public ChangeCommand deserialize(String topic, byte[] data) {
        ChangeCommand command = new ChangeCommand();
        try {
            thriftDeserializer.get().deserialize(command, data);
        } catch (Exception e) {
            log.error("Error when deserialize command data: {} ", data, e);
        }
        return command;
    }

    @Override
    public void close() {

    }
}