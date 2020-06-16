package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.damsel.fraudbusters.PaymentValidateServiceSrv;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.service.GroupCommandService;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class PaymentFraudoConfig {

    @Bean
    public PaymentValidateServiceSrv.Iface paymentValidateServiceSrv(@Value("${service.validate.payment.url}") Resource resource,
                                                                     @Value("${service.validate.payment.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(PaymentValidateServiceSrv.Iface.class);
    }

    @Bean
    public TemplateCommandService paymentTemplateCommandService(CommandSender commandSender,
                                                                @Value("${kafka.topic.fraudbusters.payment.template}") String topic) {
        return new TemplateCommandService(commandSender, topic);
    }

    @Bean
    public GroupCommandService paymentGroupCommandService(CommandSender commandSender,
                                                          @Value("${kafka.topic.fraudbusters.payment.group.list}") String topic) {
        return new GroupCommandService(commandSender, topic);
    }

}
