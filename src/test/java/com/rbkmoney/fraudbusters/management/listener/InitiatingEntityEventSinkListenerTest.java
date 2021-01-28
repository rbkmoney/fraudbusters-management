package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentTemplateReferenceService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InitiatingEntityEventSinkListenerTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";

    @Mock
    PaymentTemplateReferenceService paymentTemplateReferenceService;
    @Mock
    DefaultPaymentReferenceDaoImpl referenceDao;

    InitiatingEntityEventSinkListener initiatingEntityEventSinkListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        initiatingEntityEventSinkListener = new InitiatingEntityEventSinkListener(
                paymentTemplateReferenceService, referenceDao);
    }

    @Test
    public void listen() {
        when(referenceDao.getByPartyAndShop(PARTY_ID, null)).thenReturn(Optional.empty());
        when(referenceDao.getByPartyAndShop(PARTY_ID, SHOP_ID)).thenReturn(Optional.empty());
        when(referenceDao.getByPartyAndShop(null, null)).thenReturn(Optional.of(new DefaultPaymentReferenceModel()));
        final ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(new MerchantInfo()
                .setPartyId(PARTY_ID)
                .setShopId(SHOP_ID)
        );
        initiatingEntityEventSinkListener.listen(referenceInfo);

        verify(referenceDao, times(3)).getByPartyAndShop(any(), any());
        verify(paymentTemplateReferenceService, times(1)).sendCommandSync(any());
    }
}
