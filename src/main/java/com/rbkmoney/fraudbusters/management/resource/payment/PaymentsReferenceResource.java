package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.payment.DefaultPaymentReferenceModelToPaymentReferenceConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentReferenceModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentReferenceModelToPaymentReferenceConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.DefaultPaymentReferenceDaoImpl;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.DefaultPaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.filter.UnknownPaymentTemplateInReferenceFilter;
import com.rbkmoney.fraudbusters.management.service.payment.PaymentTemplateReferenceService;
import com.rbkmoney.fraudbusters.management.utils.*;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsReferenceResource implements PaymentsReferencesApi {

    private final PaymentReferenceDao referenceDao;
    private final DefaultPaymentReferenceDaoImpl defaultPaymentReferenceDao;
    private final UserInfoService userInfoService;
    private final PaymentUnknownTemplateFinder unknownTemplateFinder;
    private final ReferenceToCommandConverter referenceToCommandConverter;
    private final UnknownPaymentTemplateInReferenceFilter templateInReferenceFilter;
    private final CommandMapper commandMapper;
    private final PaymentTemplateReferenceService paymentTemplateReferenceService;
    private final DefaultPaymentReferenceDaoImpl defaultReferenceDao;
    private final PaymentReferenceModelToCommandConverter paymentReferenceModelToCommandConverter;
    private final PaymentReferenceModelToPaymentReferenceConverter modelToPaymentReferenceConverter;
    private final DefaultPaymentReferenceModelToPaymentReferenceConverter defaultModelToPaymentReferenceConverter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ReferencesResponse> filterDefaultReferences(@Valid String lastId, @Valid String sortOrder,
                                                                      @Valid String searchValue, @Valid String sortBy,
                                                                      @Valid String sortFieldValue,
                                                                      @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        log.info("filterReferences initiator: {} filterRequest: {}", userInfoService.getUserName(), filterRequest);
        FilterRequestUtils.prepareFilterRequest(filterRequest);
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
    public ResponseEntity<String> insertDefaultReference(@Valid PaymentReference paymentReference) {
        log.info("insertDefaultReference initiator: {} referenceModels: {}", userInfoService.getUserName(),
                paymentReference);
        var uid = UUID.randomUUID().toString();
        var defaultReferenceModel = DefaultPaymentReferenceModel.builder()
                .id(uid)
                .lastUpdateDate(paymentReference.getLastUpdateDate().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .modifiedByUser(userInfoService.getUserName())
                .partyId(paymentReference.getPartyId())
                .shopId(paymentReference.getShopId())
                .templateId(paymentReference.getTemplateId())
                .build();
        defaultReferenceDao.insert(defaultReferenceModel);
        return ResponseEntity.ok().body(uid);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> insertReferences(@Valid List<PaymentReference> paymentReference) {
        String userName = userInfoService.getUserName();
        log.info("insertReference initiator: {} referenceModels: {}", userName, paymentReference);
        List<String> unknownTemplates =
                unknownTemplateFinder.findUnknownTemplate(paymentReference, templateInReferenceFilter);
        if (!CollectionUtils.isEmpty(unknownTemplates)) {
            return new ResponseEntity<>(unknownTemplates, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<String> ids = paymentReference.stream()
                .map(referenceToCommandConverter::convert)
                .map(command -> commandMapper.mapToConcreteCommand(userName, command, CommandType.CREATE))
                .map(paymentTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ids);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeDefaultReference(String id) {
        log.info("removeDefaultReference initiator: {} id: {}", userInfoService.getUserName(), id);
        defaultReferenceDao.remove(id);
        return ResponseEntity.ok().body(id);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<String> removeReference(String id) {
        String userName = userInfoService.getUserName();
        log.info("removeReference initiator: {} id: {}", userName, id);
        PaymentReferenceModel reference = referenceDao.getById(id);
        var command = paymentReferenceModelToCommandConverter.convert(reference);
        String commandSendDeletedId = paymentTemplateReferenceService
                .sendCommandSync(commandMapper.mapToConcreteCommand(userName, command, CommandType.DELETE));
        return ResponseEntity.ok().body(commandSendDeletedId);
    }

}
