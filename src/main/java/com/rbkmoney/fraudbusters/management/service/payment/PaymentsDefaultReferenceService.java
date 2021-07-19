package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.utils.DateTimeUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsDefaultReferenceService {

    private final DefaultPaymentReferenceDaoImpl defaultPaymentReferenceDao;
    private final UserInfoService userInfoService;

    public String inertDefaultReference(PaymentReference paymentReference) {
        var uid = UUID.randomUUID().toString();
        var defaultReferenceModel = DefaultPaymentReferenceModel.builder()
                .id(uid)
                .lastUpdateDate(paymentReference.getLastUpdateDate().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .modifiedByUser(userInfoService.getUserName())
                .partyId(paymentReference.getPartyId())
                .shopId(paymentReference.getShopId())
                .templateId(paymentReference.getTemplateId())
                .build();
        defaultPaymentReferenceDao.insert(defaultReferenceModel);
        return uid;
    }

}
