package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.fraudbusters.management.converter.GroupModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.TemplateModelToCommandConverter;
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
    public PaymentServiceSrv.Iface paymentServiceSrv(@Value("${service.payment.url}") Resource resource,
                                                     @Value("${service.payment.networkTimeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(PaymentServiceSrv.Iface.class);
    }

    @Bean
    public TemplateCommandService paymentTemplateCommandService(
            CommandSender commandSender,
            TemplateModelToCommandConverter templateModelToCommandConverter,
            @Value("${kafka.topic.fraudbusters.payment.template}") String topic) {
        return new TemplateCommandService(commandSender, topic, templateModelToCommandConverter);
    }

    @Bean
    public GroupCommandService paymentGroupCommandService(CommandSender commandSender,
                                                          @Value("${kafka.topic.fraudbusters.payment.group.list}")
                                                                  String topic,
                                                          GroupModelToCommandConverter groupModelToCommandConverter) {
        return new GroupCommandService(commandSender, topic, groupModelToCommandConverter);
    }

}
