package com.vk.dwzkf.tglib.botcore.context;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class DefaultMessageContextFactory implements MessageContextFactory<MessageContext> {
    @Override
    public MessageContext create() {
        return new MessageContext();
    }
}
