package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandMapper {

    private final UserInfoService userInfoService;

    public Command mapToConcreteCommand(String userName, final Command command, CommandType commandType) {
        Command newCommand = new Command(command);
        newCommand.setCommandType(commandType)
                .setUserInfo(new UserInfo()
                        .setUserId(userName)
                );
        return newCommand;
    }

}
