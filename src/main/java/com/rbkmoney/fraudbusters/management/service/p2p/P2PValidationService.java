package com.rbkmoney.fraudbusters.management.service.p2p;

import com.rbkmoney.damsel.fraudbusters.P2PServiceSrv;
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
public class P2PValidationService implements ValidationTemplateService {

    private final P2PServiceSrv.Iface p2pServiceSrv;

    @Override
    public List<TemplateValidateError> validateTemplate(List<Template> templates) {
        try {
            return p2pServiceSrv.validateCompilationTemplate(templates).getErrors();
        } catch (TException e) {
            log.error("Error when validateTemplate: ", e);
            throw new ValidationException(e);
        }
    }

}
