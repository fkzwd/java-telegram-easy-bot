package com.vk.dwzkf.tglib.botcore.context;

import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChainConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@RequiredArgsConstructor
public class MessageContextFiller {
    private final MessageFillerChainConfig messageFillerChainConfig;

    public MessageContext fillContext(MessageContext messageContext, Update update) {
        messageContext.setUpdate(update);
        fillContext(messageContext);
        return messageContext;
    }

    public void fillContext(MessageContext messageContext) {
        messageFillerChainConfig.createChain().fillContext(messageContext);
    }
}
