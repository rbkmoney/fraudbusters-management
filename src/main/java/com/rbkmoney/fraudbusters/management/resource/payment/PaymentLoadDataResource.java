package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.fraudbusters.management.utils.parser.CsvFraudPaymentParser;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsLoadDataApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentLoadDataResource implements PaymentsLoadDataApi {

    private final PaymentServiceSrv.Iface paymentServiceSrv;
    private final CsvFraudPaymentParser csvFraudPaymentParser;
    private final UserInfoService userInfoService;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<Void> loadFraudPayments(@Valid MultipartFile file) {
        if (csvFraudPaymentParser.hasCsvFormat(file)) {
            try {
                List<FraudPayment> fraudPayments = csvFraudPaymentParser.parse(file.getInputStream());
                log.info("PaymentLoadDataResource loadFraudOperation initiator: {} fraudPaymentRecords: {}",
                        userInfoService.getUserName(), fraudPayments);
                paymentServiceSrv.insertFraudPayments(fraudPayments);

                log.info("PaymentLoadDataResource loaded fraudPayments: {}", fraudPayments);

            } catch (IOException | TException e) {
                log.error("PaymentLoadDataResource error when loadFraudOperation e: ", e);
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok().build();
    }

}
