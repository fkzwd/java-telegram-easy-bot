package com.vk.dwzkf.tglib.example.echobot.text;

import com.vk.dwzkf.tglib.botcore.annotations.TextHandler;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.handlers.DefaultTextMessageHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
@Service
@TextHandler
@Order(1)
public class EchoTextMessageHandler extends DefaultTextMessageHandler  {
    @Override
    public boolean match(MessageContext messageContext) {
        return true;
    }
    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        messageContext.doAnswer("[ECHO]: "+messageContext.getMessage());
    }
}
