package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.converter.payment.DefaultPaymentReferenceModelToPaymentReferenceConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentReferenceModelToPaymentReferenceConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.filter.UnknownPaymentTemplateInReferenceFilter;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsDefaultReferenceService;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentsReferenceService;
import com.rbkmoney.fraudbusters.management.utils.FilterRequestUtils;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.PaymentUnknownTemplateFinder;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import com.rbkmoney.swag.fraudbusters.management.api.PaymentsReferencesApi;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import com.rbkmoney.swag.fraudbusters.management.model.ReferencesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsReferenceResource implements PaymentsReferencesApi {

    private final PaymentReferenceDao referenceDao;
    private final UserInfoService userInfoService;
    private final PaymentReferenceModelToPaymentReferenceConverter modelToPaymentReferenceConverter;
    private final PaymentsReferenceService paymentsReferenceService;
    private final DefaultPaymentReferenceDaoImpl defaultPaymentReferenceDao;
    private final PaymentsDefaultReferenceService paymentsDefaultReferenceService;
    private final DefaultPaymentReferenceModelToPaymentReferenceConverter defaultModelToPaymentReferenceConverter;
    private final PaymentUnknownTemplateFinder unknownTemplateFinder;
    private final UnknownPaymentTemplateInReferenceFilter templateInReferenceFilter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ReferencesResponse> filterDefaultReferences(@Valid String lastId, @Valid String sortOrder,
                                                                      @Valid String searchValue, @Valid String sortBy,
                                                                      @Valid String sortFieldValue,
                                                                      @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        log.info("filterReferences initiator: {} filterRequest: {}", userInfoService.getUserName(), filterRequest);
        filterRequest.setSearchValue(FilterRequestUtils.prepareSearchValue(filterRequest.getSearchValue()));
        List<DefaultPaymentReferenceModel> paymentReferenceModels =
                defaultPaymentReferenceDao.filterReferences(filterRequest);
        Integer count = defaultPaymentReferenceDao.countFilterModel(searchValue);
        return ResponseEntity.ok().body(new ReferencesResponse()
                .count(count)
                .result(paymentReferenceModels.stream()
                        .map(defaultModelToPaymentReferenceConverter::convert)
                        .collect(Collectors.toList())));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> insertDefaultReference(@Valid PaymentReference paymentReference) {
        log.info("insertDefaultReference initiator: {} referenceModels: {}", userInfoService.getUserName(),
                paymentReference);
        String uid = paymentsDefaultReferenceService.insertDefaultReference(paymentReference);
        return ResponseEntity.ok().body(uid);
    }


    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeDefaultReference(String id) {
        log.info("removeDefaultReference initiator: {} id: {}", userInfoService.getUserName(), id);
        defaultPaymentReferenceDao.remove(id);
        return ResponseEntity.ok().body(id);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ReferencesResponse> filterReferences(@Valid String lastId, @Valid String sortOrder,
                                                               @Valid String searchValue, @Valid String sortBy,
                                                               @Valid String sortFieldValue, @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        log.info("filterReferences initiator: {} filterRequest: {}", userInfoService.getUserName(), filterRequest);
        List<PaymentReferenceModel> paymentReferenceModels = referenceDao.filterReferences(filterRequest);
        Integer count = referenceDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(new ReferencesResponse()
                .count(count)
                .result(paymentReferenceModels.stream()
                        .map(modelToPaymentReferenceConverter::convert)
                        .collect(Collectors.toList())));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertReferences(@Valid List<PaymentReference> paymentReference) {
        String userName = userInfoService.getUserName();
        log.info("insertReference initiator: {} referenceModels: {}", userName, paymentReference);
        List<String> unknownTemplates =
                unknownTemplateFinder.find(paymentReference, templateInReferenceFilter);
        if (!CollectionUtils.isEmpty(unknownTemplates)) {
            return new ResponseEntity<>(unknownTemplates, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<String> ids = paymentsReferenceService.insertReferences(paymentReference, userName);
        return ResponseEntity.ok().body(ids);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeReference(String id) {
        String userName = userInfoService.getUserName();
        log.info("removeReference initiator: {} id: {}", userName, id);
        String commandSendDeletedId = paymentsReferenceService.removeReference(id, userName);
        return ResponseEntity.ok().body(commandSendDeletedId);
    }

}
