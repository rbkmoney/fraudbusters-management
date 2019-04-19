package com.rbkmoney.fraudbusters.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ServletComponentScan
@SpringBootApplication
public class FraudbustersManagementApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudbustersManagementApplication.class, args);
    }

}
