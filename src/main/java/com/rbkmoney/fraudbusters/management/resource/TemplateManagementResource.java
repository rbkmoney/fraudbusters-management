package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.management.converter.ListRecordToRowConverter;
import com.rbkmoney.fraudbusters.management.converter.WbListRecordsToListRecordConverter;
import com.rbkmoney.fraudbusters.management.dao.wblist.WbListDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.service.FraudbustersCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TemplateManagementResource {

    private final FraudbustersCommandService fraudbustersCommandService;

    @PostMapping(value = "/template")
    public ResponseEntity<String> insertTemplate(@Validated @RequestBody TemplateModel templateModel) throws ExecutionException, InterruptedException {
        log.info("TemplateManagementResource insertTemplate templateModel: {}", templateModel);
        Command command = createCommand(templateModel, CommandType.CREATE);
        String idMessage = fraudbustersCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

    private Command createCommand(@RequestBody @Validated TemplateModel templateModel, CommandType type) {
        Command command = new Command();
        Template template = new Template();
        template.setTemplate(templateModel.getTemplate().getBytes());
        command.setCommandBody(CommandBody.template(template));
        command.setCommandType(type);
        return command;
    }

    @DeleteMapping(value = "/template")
    public ResponseEntity<String> removeTemplate(@Validated @RequestBody TemplateModel templateModel) throws ExecutionException, InterruptedException {
        log.info("TemplateManagementResource removeTemplate templateModel: {}", templateModel);
        Command command = createCommand(templateModel, CommandType.DELETE);
        String idMessage = fraudbustersCommandService.sendCommandSync(command);
        return ResponseEntity.ok().body(idMessage);
    }

}
