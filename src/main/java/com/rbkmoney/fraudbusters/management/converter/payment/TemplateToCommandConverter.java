package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TemplateToCommandConverter
        implements Converter<com.rbkmoney.swag.fraudbusters.management.model.Template, Command> {

    @Override
    @NonNull
    public Command convert(com.rbkmoney.swag.fraudbusters.management.model.Template templateModel) {
        var command = new Command();
        var template = new Template();
        template.setId(templateModel.getId());
        template.setTemplate(templateModel.getTemplate().getBytes());
        command.setCommandBody(CommandBody.template(template));
        return command;
    }
}
