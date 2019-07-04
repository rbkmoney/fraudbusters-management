package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.wb_list.Event;
import com.rbkmoney.fraudbusters.management.serializer.CommandFraudDeserializer;
import com.rbkmoney.fraudbusters.management.serializer.EventDeserializer;
import com.rbkmoney.serializer.ThriftSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.LoggingErrorHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;

@Configuration
public class KafkaConfig {

    private static final String GROUP_ID = "FraudBusterListener";
    private static final String EARLIEST = "earliest";
    public static final String PKCS_12 = "PKCS12";

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${kafka.ssl.server-password}")
    private String serverStorePassword;
    @Value("${kafka.ssl.server-keystore-location}")
    private String serverStoreCertPath;
    @Value("${kafka.ssl.keystore-password}")
    private String keyStorePassword;
    @Value("${kafka.ssl.key-password}")
    private String keyPassword;
    @Value("${kafka.ssl.keystore-location}")
    private String clientStoreCertPath;
    @Value("${kafka.ssl.enable}")
    private boolean kafkaSslEnable;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        props.put(MAX_POLL_RECORDS_CONFIG, 1);
        sslConfigure(props);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Event> consumerFactory() {
        Map<String, Object> configs = consumerConfigs();
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    @Autowired
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Event>> kafkaListenerContainerFactory(ConsumerFactory<String, Event> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Event> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setErrorHandler(new LoggingErrorHandler());
        return factory;
    }

    //Template
    @Bean
    public ConsumerFactory<String, Command> consumerTemplateFactory() {
        Map<String, Object> configs = consumerConfigs();
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CommandFraudDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    @Autowired
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Command>> kafkaTemplateListenerContainerFactory(ConsumerFactory<String, Command> consumerTemplateFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerTemplateFactory);
        factory.setErrorHandler(new LoggingErrorHandler());
        factory.setConcurrency(1);
        return factory;
    }


    //Reference
    @Bean
    public ConsumerFactory<String, Command> consumerReferenceFactory() {
        Map<String, Object> configs = consumerConfigs();
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CommandFraudDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    @Autowired
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Command>> kafkaReferenceListenerContainerFactory(ConsumerFactory<String, Command> consumerReferenceFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Command> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerReferenceFactory);
        factory.setErrorHandler(new LoggingErrorHandler());
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    public ProducerFactory<String, TBase> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        sslConfigure(configProps);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private void sslConfigure(Map<String, Object> configProps) {
        if (kafkaSslEnable) {
            configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(serverStoreCertPath).getAbsolutePath());
            configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, serverStorePassword);
            configProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, PKCS_12);
            configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, new File(clientStoreCertPath).getAbsolutePath());
            configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePassword);
            configProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);
        }
    }

    @Bean
    public KafkaTemplate<String, TBase> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
