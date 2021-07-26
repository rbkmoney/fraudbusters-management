package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentInfoResultToPaymentsConverter;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsHistoricalDataApi;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentHistoricalDataResource implements PaymentsHistoricalDataApi {

    private final HistoricalDataServiceSrv.Iface historicalDataServiceSrv;
    private final PaymentInfoResultToPaymentsConverter paymentInfoResultToPaymentsConverter;

    @SneakyThrows
    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<PaymentResponse> filterPaymentsInfo(@Valid String lastId, @Valid String sortOrder,
                                                              @Valid String searchValue, @Valid String sortBy,
                                                              @Valid String sortFieldValue, @Valid Integer size,
                                                              @Valid String partyId, @Valid String shopId,
                                                              @Valid String paymentId, @Valid String status,
                                                              @Valid String email, @Valid String providerCountry,
                                                              @Valid String cardToken, @Valid String fingerprint,
                                                              @Valid String terminal) {
        log.info("-> filterPaymentsInfo lastId: {} size: {} partyId: {} shopId: {} paymentId: {} status: {} email: {}" +
                        " providerCountry: {} cardToken: {} fingerprint: {} terminal: {}",
                lastId, size, partyId, shopId, paymentId, status, email, providerCountry, cardToken, fingerprint,
                terminal);
        var payments = historicalDataServiceSrv.getPayments(
                createFilter(partyId, shopId, paymentId, status, email, providerCountry, cardToken, fingerprint,
                        terminal),
                createPage(lastId, size),
                createPage(sortOrder, sortBy));
        var paymentResponse = new PaymentResponse()
                .result(paymentInfoResultToPaymentsConverter.convert(payments.getData().getPayments()))
                .continuationId(payments.getContinuationId());
        log.info("<- filterPaymentsInfo paymentResponse: {}", paymentResponse);
        return ResponseEntity.ok(paymentResponse);
    }

    private Sort createPage(String sortOrder, String sortBy) {
        return new Sort()
                .setField(sortBy)
                .setOrder(sortOrder != null ? SortOrder.valueOf(sortOrder) : SortOrder.ASC);
    }

    private Page createPage(String lastId, Integer size) {
        return new Page()
                .setContinuationId(lastId)
                .setSize(size);
    }

    private Filter createFilter(String partyId, String shopId, String paymentId, String status, String email,
                                String providerCountry, String cardToken, String fingerprint, String terminal) {
        return new Filter()
                .setEmail(email)
                .setCardToken(cardToken)
                .setPaymentId(paymentId)
                .setFingerprint(fingerprint)
                .setPartyId(partyId)
                .setProviderCountry(providerCountry)
                .setShopId(shopId)
                .setStatus(status)
                .setTerminal(terminal);
    }
}
