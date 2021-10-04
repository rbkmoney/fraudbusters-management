package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.testcontainers.annotations.KafkaSpringBootTest;
import com.rbkmoney.testcontainers.annotations.kafka.KafkaTestcontainer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaTestcontainer(topicsKeys = {
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
@KafkaSpringBootTest
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
public @interface KafkaITest {
}
