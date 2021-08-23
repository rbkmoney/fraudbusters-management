package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.swag.fraudbusters.management.model.ApplyRuleOnHistoricalDataSetRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class ApplyRuleOnHistoricalRequestToEmulationRuleApplyRequestConverter
        implements Converter<ApplyRuleOnHistoricalDataSetRequest, EmulationRuleApplyRequest> {

    public static final String EMULATION_TEMPLATE = "emulation_template";
    private final PaymentApiToPaymentConverter paymentApiToPaymentConverter;

    @NonNull
    @Override
    public EmulationRuleApplyRequest convert(ApplyRuleOnHistoricalDataSetRequest request) {
        var emulationRule = new EmulationRule();
        if (request.getReference() != null) {
            emulationRule.setCascadingEmulation(new CascasdingTemplateEmulation()
                    .setRef(new TemplateReference()
                            .setPartyId(request.getReference().getPartyId())
                            .setShopId(request.getReference().getShopId()))
                    .setTemplate(new com.rbkmoney.damsel.fraudbusters.Template()
                            .setId(EMULATION_TEMPLATE)
                            .setTemplate(request.getTemplate().getBytes()))
                    .setRuleSetTimestamp(request.getRuleSetTimestamp().toString()));
        } else {
            emulationRule.setTemplateEmulation(new OnlyTemplateEmulation()
                    .setTemplate(new com.rbkmoney.damsel.fraudbusters.Template()
                            .setId(EMULATION_TEMPLATE)
                            .setTemplate(request.getTemplate().getBytes())));
        }
        return new EmulationRuleApplyRequest()
                .setEmulationRule(emulationRule)
                .setTransactions(request.getRecords().stream()
                        .map(paymentApiToPaymentConverter::convert)
                        .collect(Collectors.toSet()));
    }


}