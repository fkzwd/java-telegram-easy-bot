package com.vk.dwzkf.tglib.botcore.context;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public interface MessageContextFactory<T extends MessageContext> {
    T create();
}
