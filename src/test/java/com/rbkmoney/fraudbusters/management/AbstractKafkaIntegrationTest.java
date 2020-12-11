package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.fraudbusters.management.config.KafkaConfig;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {KafkaConfig.class},
        initializers = AbstractKafkaIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractKafkaIntegrationTest {

    public static final String KAFKA_DOCKER_VERSION = "5.0.1";
    public static String UNKNOWN_INITIATING_ENTITY = "unknown_initiating_entity";


    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag(KAFKA_DOCKER_VERSION))
            .withEmbeddedZookeeper()
            .withCommand("bash -c 'echo Waiting for Kafka to be ready... && \n" +
                    "                                cub kafka-ready -b broker:9092 1 60 && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic wb-list-event-sink  && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic wb-list-command && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic " + UNKNOWN_INITIATING_ENTITY + " && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_p2p && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_reference && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_p2p_reference && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_list && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_p2p_list && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_reference && \n" +
                    "                                kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_p2p_reference && \n" +
                    "                                echo Waiting 60 seconds for Connect to be ready... && \n" +
                    "                                sleep 60'");
    ;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    public static <T> Consumer<String, T> createConsumer(Class clazz) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    public static <T> Producer<String, T> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "CLIENT");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        return new KafkaProducer<>(props);
    }

}
