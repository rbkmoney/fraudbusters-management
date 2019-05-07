package com.rbkmoney.fraudbusters.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import javax.annotation.PreDestroy;

@ServletComponentScan
@SpringBootApplication
public class FraudbustersManagementApplication extends SpringApplication {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public static void main(String[] args) {
        SpringApplication.run(FraudbustersManagementApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        registry.stop();
    }
}