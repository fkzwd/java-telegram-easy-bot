package com.vk.dwzkf.tglib.botcore.context.filler_chain;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public abstract class DefaultContextFiller implements ContextFiller<MessageContext> {
    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
