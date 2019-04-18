package com.rbkmoney.fraudbusters.management.serializer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.Map;

@Slf4j
public class ThriftSerializer<T extends TBase> implements Serializer<T> {

    private final ThreadLocal<TSerializer> thriftSerializer = ThreadLocal.withInitial(TSerializer::new);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, T event) {
        try {
            return thriftSerializer.get().serialize(event);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }
}