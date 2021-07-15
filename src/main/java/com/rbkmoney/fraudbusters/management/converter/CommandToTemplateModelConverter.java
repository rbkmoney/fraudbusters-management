package com.rbkmoney.fraudbusters.management.converter;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommandToTemplateModelConverter implements Converter<Command, TemplateModel> {

    @Override
    public TemplateModel convert(Command command) {
        var model = new TemplateModel();
        var template = command.getCommandBody().getTemplate();
        model.setId(template.getId());
        model.setTemplate(new String(template.getTemplate()));
        return model;
    }
}
