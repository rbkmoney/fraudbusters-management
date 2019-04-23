package com.rbkmoney.fraudbusters.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PreDestroy;

@EnableWebMvc
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
