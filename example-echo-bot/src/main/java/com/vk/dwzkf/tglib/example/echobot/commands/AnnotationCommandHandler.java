package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.annotations.RouteCommandHandler;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 16.12.2024
 */
@RouteCommand(command = "annotation")
@Component
public class AnnotationCommandHandler {
    @RouteCommandHandler
    public void handle(MessageContext ctx) throws BotCoreException {
        ctx.doAnswer("[ANNOTATED]: "+ctx.getRawData());
    }
}
