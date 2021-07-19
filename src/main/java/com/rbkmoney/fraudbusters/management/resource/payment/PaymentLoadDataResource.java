package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.service.payment.PaymentLoadDataService;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsLoadDataApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentLoadDataResource implements PaymentsLoadDataApi {

    private final PaymentLoadDataService paymentLoadDataService;
    private final UserInfoService userInfoService;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> loadFraudPayments(@Valid MultipartFile file) {
        String userName = userInfoService.getUserName();
        paymentLoadDataService.loadFraudPayments(file, userName);
        return ResponseEntity.ok().build();
    }

}
