package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.fraudbusters.management.FraudbustersManagementApplication;
import com.rbkmoney.testcontainers.annotations.kafka.KafkaTestcontainerSingleton;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaConsumerConfig;
import com.rbkmoney.testcontainers.annotations.kafka.config.KafkaProducerConfig;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaTestcontainerSingleton(topicsKeys = {
        "kafka.topic.fraudbusters.unknown-initiating-entity",
        "kafka.topic.wblist.command",
        "kafka.topic.wblist.event.sink",
        "kafka.topic.fraudbusters.payment.template",
        "kafka.topic.fraudbusters.payment.reference",
        "kafka.topic.fraudbusters.payment.group.list",
        "kafka.topic.fraudbusters.payment.group.reference",
        "kafka.topic.fraudbusters.p2p.template",
        "kafka.topic.fraudbusters.p2p.reference",
        "kafka.topic.fraudbusters.p2p.group.list",
        "kafka.topic.fraudbusters.p2p.group.reference"
})
@ContextConfiguration(
        classes = {
                FraudbustersManagementApplication.class,
                KafkaProducerConfig.class,
                KafkaConsumerConfig.class})
@DirtiesContext
public @interface KafkaITest {
}
