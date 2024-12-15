package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.TextMessageHandleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@RequiredArgsConstructor
@Slf4j
//TODO: raw types
public class TextMessageHandlerRegistry {
    private final List<TextMessageHandler> messageHandlers = new LinkedList<>();
    private TextMessageHandler notFoundTextMessageHandler = new NotFoundTextMessageHandler();

    public void addHandler(TextMessageHandler textMessageHandler) {
        messageHandlers.add(textMessageHandler);
    }

    public void addHandlers(List<TextMessageHandler> textMessageHandlers) {
        messageHandlers.addAll(textMessageHandlers);
    }

    //TODO: raw
    public void handleText(MessageContext messageContext) throws BotCoreException {
        for (TextMessageHandler messageHandler : messageHandlers) {
            if (!messageHandler.supports().isAssignableFrom(messageContext.getClass())) {
                continue;
            }
            if (!messageHandler.match(messageContext)) {
                continue;
            }
            try {
                messageHandler.handle(messageContext);
            } catch (BotCoreException e) {
                throw  e;
            } catch (Exception exception) {
                TextMessageHandleException result = new TextMessageHandleException(
                        "Error while handle message",
                        exception
                );
                result.setMessage(String.format("Ошибка \"%s\" для ввода \"%s\"", exception.getMessage(), messageContext.getRawData()));
                throw result;
            }
            return;
        }
        notFoundTextMessageHandler.handle(messageContext);
    }
}
