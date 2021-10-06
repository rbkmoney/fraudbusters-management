package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentTemplateReferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class InitiatingEntityEventSinkListenerTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";

    @Mock
    PaymentTemplateReferenceService paymentTemplateReferenceService;
    @Mock
    DefaultPaymentReferenceDaoImpl defaultPaymentReferenceDao;
    @Mock
    PaymentReferenceDaoImpl paymentReferenceDaoImpl;

    InitiatingEntityEventSinkListener initiatingEntityEventSinkListener;

    @BeforeEach
    void init() {
        initiatingEntityEventSinkListener = new InitiatingEntityEventSinkListener(
                paymentTemplateReferenceService, defaultPaymentReferenceDao, paymentReferenceDaoImpl);
    }

    @Test
    void listen() {
        when(defaultPaymentReferenceDao.getByPartyAndShop(PARTY_ID, null)).thenReturn(Optional.empty());
        when(defaultPaymentReferenceDao.getByPartyAndShop(PARTY_ID, SHOP_ID)).thenReturn(Optional.empty());
        when(defaultPaymentReferenceDao.getByPartyAndShop(null, null))
                .thenReturn(Optional.of(new DefaultPaymentReferenceModel()));
        final ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(new MerchantInfo()
                .setPartyId(PARTY_ID)
                .setShopId(SHOP_ID)
        );
        initiatingEntityEventSinkListener.listen(referenceInfo);

        verify(defaultPaymentReferenceDao, times(3)).getByPartyAndShop(any(), any());
        verify(paymentTemplateReferenceService, times(1)).sendCommandSync(any());
    }
}
