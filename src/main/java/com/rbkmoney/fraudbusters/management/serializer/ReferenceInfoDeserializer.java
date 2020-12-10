package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReferenceInfoDeserializer extends AbstractThriftDeserializer<ReferenceInfo> {

    @Override
    public ReferenceInfo deserialize(String topic, byte[] data) {
        return deserialize(data, new ReferenceInfo());
    }
}
