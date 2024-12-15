package com.vk.dwzkf.tglib.botcore.context.filler_chain;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public interface ContextFiller<T extends MessageContext> {
    void fill(T ctx, MessageFillerChain chain);
    Class<T> supports();
}
