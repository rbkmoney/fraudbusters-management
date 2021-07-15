package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.PaymentInfoResult;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsHistoricalDataApi;
import com.rbkmoney.swag.fraudbusters.management.model.*;
import com.rbkmoney.swag.fraudbusters.management.model.Error;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentHistoricalDataResource implements PaymentsHistoricalDataApi {

    private final HistoricalDataServiceSrv.Iface historicalDataServiceSrv;

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
        PaymentInfoResult payments = historicalDataServiceSrv.getPayments(
                createFilter(partyId, shopId, paymentId, status, email, providerCountry, cardToken, fingerprint,
                        terminal),
                createPage(lastId, size));
        return ResponseEntity.ok(new PaymentResponse()
                .result(mapPayments(payments))
                .continuationId(payments.getContinuationId())
        );
    }

    private Page createPage(String lastId, Integer size) {
        return new Page()
                .setContinuationId(lastId)
                .setSize(size);
    }

    private List<Payment> mapPayments(PaymentInfoResult payments) {
        return payments.getPayments().stream()
                .map(paymentInfo -> new Payment()
                        .cardToken(paymentInfo.getCardToken())
                        .amount(paymentInfo.getAmount())
                        .clientInfo(new ClientInfo()
                                .email(paymentInfo.getClientInfo().getEmail())
                                .fingerprint(paymentInfo.getClientInfo().getFingerprint())
                                .ip(paymentInfo.getClientInfo().getIp())
                        )
                        .currency(paymentInfo.getCurrency().symbolic_code)
                        .error(new Error()
                                .errorCode(paymentInfo.getError().getErrorCode())
                                .errorReason(paymentInfo.getError().getErrorReason()))
                        .eventTime(DateTimeUtils.toDate(paymentInfo.getEventTime()))
                        .id(paymentInfo.getId())
                        .merchantInfo(new MerchantInfo()
                                .partyId(paymentInfo.getMerchantInfo().getPartyId())
                                .shopId(paymentInfo.getMerchantInfo().shop_id)
                        )
                        .paymentCountry(paymentInfo.getPaymentCountry())
                        .paymentSystem(paymentInfo.getPaymentSystem().name())
                        .paymentTool(paymentInfo.getPaymentTool())
                        .provider(new ProviderInfo()
                                .providerId(paymentInfo.getProvider().getProviderId())
                                .country(paymentInfo.getProvider().getCountry())
                                .terminalId(paymentInfo.getProvider().getTerminalId()))
                        .status(Payment.StatusEnum.valueOf(paymentInfo.getStatus().name()))
                ).collect(Collectors.toList());
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
