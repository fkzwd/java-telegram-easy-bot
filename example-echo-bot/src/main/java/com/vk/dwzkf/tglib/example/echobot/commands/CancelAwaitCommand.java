package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.annotations.RouteCommandHandler;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.input.InputWaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 17.12.2024
 */
@Service
@RouteCommand(command = "cancelawait")
@RequiredArgsConstructor
public class CancelAwaitCommand {
    private final InputWaiterService inputWaiterService;

    @RouteCommandHandler
    public void handle(MessageContext messageContext) throws BotCoreException {
        List<MessageContext> messageContexts = inputWaiterService.cancelAll(messageContext);
        if (messageContexts.isEmpty()) {
            messageContext.doReply("Нет ожидающих команд");
        }
        for (MessageContext context : messageContexts) {
            context.doReply("Команда остановлена");
        }
    }
}
