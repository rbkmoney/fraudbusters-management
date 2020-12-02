package com.rbkmoney.fraudbusters.management.service;

import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateValidateError;

import java.util.List;

public interface ValidationTemplateService {

    List<TemplateValidateError> validateTemplate(Template template);

}