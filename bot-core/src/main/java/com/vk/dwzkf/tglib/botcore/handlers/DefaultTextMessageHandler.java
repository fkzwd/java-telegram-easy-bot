package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;

/**
 * @author Roman Shageev
 * @since 05.11.2023
 */
public abstract class DefaultTextMessageHandler implements TextMessageHandler<MessageContext> {
    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
