package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.converter.payment.TemplateModelToTemplateConverter;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.service.PaymentEmulateService;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsEmulationsApi;
import com.rbkmoney.swag.fraudbusters.management.model.EmulateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentEmulateResource implements PaymentsEmulationsApi {

    private final PaymentEmulateService paymentEmulateService;
    private final UserInfoService userInfoService;
    private final TemplateModelToTemplateConverter templateModelToTemplateConverterImpl;

    @Override
    @PreAuthorize("hasAnyRole('fraud-support', 'fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<EmulateResponse> getTemplatesFlow(@NotNull @Valid String partyId,
                                                            @NotNull @Valid String shopId) {
        log.info("EmulateResource getRulesByPartyAndShop initiator: {} partyId: {} shopId: {}",
                userInfoService.getUserName(), partyId, shopId);
        List<TemplateModel> resultModels = paymentEmulateService.getTemplatesFlow(partyId, shopId);
        log.info("EmulateResource getRulesByPartyAndShop result: {}", resultModels);
        return ResponseEntity.ok().body(new EmulateResponse()
                .result(resultModels.stream()
                        .filter(Objects::nonNull)
                        .map(templateModelToTemplateConverterImpl::destinationToSource)
                        .collect(Collectors.toList()))
        );
    }

}
