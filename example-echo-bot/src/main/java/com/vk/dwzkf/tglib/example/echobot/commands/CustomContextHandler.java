package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.annotations.RouteCommandHandler;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.example.echobot.context.CustomContext;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@RouteCommand(command = "custom")
@Component
public class CustomContextHandler {
    @RouteCommandHandler
    public void handle(CustomContext context) throws BotCoreException {
        context.doAnswer(
                "[<code>%s</code>]\ncommand: <b>%s</b>\nargs: <b>%s</b>".formatted(
                        context.getUuid(),
                        context.getCommandContext().getCommand(),
                        context.getCommandContext().getRawArgs()
                ));
    }
}
