package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.fraudbusters.management.utils.CSVFraudPaymentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentLoadDataResource {

    private final PaymentServiceSrv.Iface paymentServiceSrv;
    private final CSVFraudPaymentParser csvFraudPaymentParser;

    @PostMapping(value = "/fraud/load")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public void loadFraudOperation(Principal principal, @RequestParam("file") MultipartFile file) throws TException {
        if (csvFraudPaymentParser.hasCSVFormat(file)) {
            try {
                List<FraudPayment> fraudPayments = csvFraudPaymentParser.parse(file.getInputStream());
                log.info("PaymentLoadDataResource loadFraudOperation initiator: {} fraudPaymentRecords: {}",
                        principal.getName(),
                        fraudPayments);
                paymentServiceSrv.insertFraudPayments(fraudPayments);

                log.info("PaymentLoadDataResource loaded fraudPayments: {}", fraudPayments);
            } catch (IOException e) {
                log.error("PaymentLoadDataResource error when loadFraudOperation e: ", e);
                throw new RuntimeException(e);
            }
        }
    }
}
