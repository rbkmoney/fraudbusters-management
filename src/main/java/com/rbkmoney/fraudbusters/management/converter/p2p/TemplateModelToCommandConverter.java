package com.rbkmoney.fraudbusters.management.converter.p2p;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TemplateModelToCommandConverter implements Converter<TemplateModel, Command> {

    @Override
    @NonNull
    public Command convert(TemplateModel templateModel) {
        Command command = new Command();
        Template template = new Template();
        template.setId(templateModel.getId());
        template.setTemplate(templateModel.getTemplate().getBytes());
        command.setCommandBody(CommandBody.template(template));
        return command;
    }
}
