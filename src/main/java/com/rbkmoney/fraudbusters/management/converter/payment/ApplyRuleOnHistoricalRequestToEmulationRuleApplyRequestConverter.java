package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.swag.fraudbusters.management.model.ApplyRuleOnHistoricalDataSetRequest;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        PaymentReference reference = request.getReference();
        if (reference != null && StringUtils.hasText(reference.getPartyId())) {
            emulationRule.setCascadingEmulation(new CascasdingTemplateEmulation()
                    .setRef(new TemplateReference()
                            .setPartyId(reference.getPartyId())
                            .setShopId(reference.getShopId())
                            .setTemplateId(EMULATION_TEMPLATE))
                    .setTemplate(new com.rbkmoney.damsel.fraudbusters.Template()
                            .setId(EMULATION_TEMPLATE)
                            .setTemplate(request.getTemplate().getBytes()))
                    .setRuleSetTimestamp(request.getRuleSetTimestamp() != null
                            ? request.getRuleSetTimestamp().toString()
                            : null));
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
