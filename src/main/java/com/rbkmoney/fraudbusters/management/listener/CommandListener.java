package com.rbkmoney.fraudbusters.management.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.function.Consumer;

@Slf4j
public abstract class CommandListener {

    protected <R> void handle(Command command, Converter<Command, R> converter, Consumer<R> createConsumer,
                          Consumer<R> deleteConsumer) {
        R model = converter.convert(command);
        switch (command.getCommandType()) {
            case CREATE:
                createConsumer.accept(model);
                break;
            case DELETE:
                deleteConsumer.accept(model);
                break;
            default:
                log.warn("CommandType not found! groupModel: {}", model);
        }
    }
}
