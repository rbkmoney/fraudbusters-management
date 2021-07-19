package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.fraudbusters.management.converter.p2p.TemplateModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.GroupToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentGroupReferenceModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.service.CommandSender;
import com.rbkmoney.fraudbusters.management.service.TemplateCommandService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentGroupCommandService;
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
    public HistoricalDataServiceSrv.Iface historicalDataServiceSrv(@Value("${service.payment.url}") Resource resource,
                                                                   @Value("${service.payment.networkTimeout}")
                                                                           int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(HistoricalDataServiceSrv.Iface.class);
    }

    @Bean
    public TemplateCommandService paymentTemplateCommandService(
            CommandSender commandSender,
            TemplateModelToCommandConverter templateModelToCommandConverter,
            @Value("${kafka.topic.fraudbusters.payment.template}") String topic) {
        return new TemplateCommandService(commandSender, topic, templateModelToCommandConverter);
    }

    @Bean
    public PaymentGroupCommandService paymentGroupCommandService(
            CommandSender commandSender,
            @Value("${kafka.topic.fraudbusters.payment.group.list}")
                    String topic,
            GroupToCommandConverter groupToCommandConverter,
            PaymentGroupReferenceModelToCommandConverter groupReferenceToCommandConverter) {
        return new PaymentGroupCommandService(commandSender, topic, groupToCommandConverter,
                groupReferenceToCommandConverter);
    }

}
