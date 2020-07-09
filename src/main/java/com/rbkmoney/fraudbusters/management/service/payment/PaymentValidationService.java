package com.rbkmoney.fraudbusters.management.service.payment;

import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;
import com.rbkmoney.fraudbusters.management.exception.ValidationException;
import com.rbkmoney.fraudbusters.management.service.ValidationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentValidationService implements ValidationTemplateService {

    private final PaymentServiceSrv.Iface paymentServiceSrv;

    @Override
    public List<TemplateValidateError> validateTemplate(List<Template> templates) {
        try {
            return paymentServiceSrv.validateCompilationTemplate(templates).getErrors();
        } catch (TException e) {
            log.error("Error when validateTemplate");
            throw new ValidationException(e);
        }
    }

}
