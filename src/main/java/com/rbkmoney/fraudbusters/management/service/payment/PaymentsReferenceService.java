package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.fraudbusters.management.converter.payment.PaymentReferenceModelToCommandConverter;
import com.rbkmoney.fraudbusters.management.converter.payment.ReferenceToCommandConverter;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.filter.UnknownPaymentTemplateInReferenceFilter;
import com.rbkmoney.fraudbusters.management.utils.CommandMapper;
import com.rbkmoney.fraudbusters.management.utils.PaymentUnknownTemplateFinder;
import com.rbkmoney.swag.fraudbusters.management.model.PaymentReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsReferenceService {

    private final PaymentReferenceDao referenceDao;
    private final CommandMapper commandMapper;
    private final PaymentTemplateReferenceService paymentTemplateReferenceService;
    private final PaymentReferenceModelToCommandConverter paymentReferenceModelToCommandConverter;
    private final ReferenceToCommandConverter referenceToCommandConverter;

    public String removeReference(String id, String userName) {
        PaymentReferenceModel reference = referenceDao.getById(id);
        var command = paymentReferenceModelToCommandConverter.convert(reference);
        return paymentTemplateReferenceService
                .sendCommandSync(commandMapper.mapToConcreteCommand(userName, command, CommandType.DELETE));
    }

    public List<String> insertReferences(List<PaymentReference> paymentReference, String initiator) {
        return paymentReference.stream()
                .map(referenceToCommandConverter::convert)
                .map(command -> commandMapper.mapToConcreteCommand(initiator, command, CommandType.CREATE))
                .map(paymentTemplateReferenceService::sendCommandSync)
                .collect(Collectors.toList());
    }
}
