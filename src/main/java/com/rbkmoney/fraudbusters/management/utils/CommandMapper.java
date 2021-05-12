package com.rbkmoney.fraudbusters.management.utils;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandType;
import com.rbkmoney.damsel.fraudbusters.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class CommandMapper {

    private final UserInfoService userInfoService;

    public Command mapToConcreteCommand(final Principal principal, final Command command, CommandType commandType) {
        Command commandNew = new Command(command);
        commandNew.setCommandType(commandType)
                .setUserInfo(new UserInfo()
                        .setUserId(userInfoService.getUserName(principal))
                );
        return commandNew;
    }

}
