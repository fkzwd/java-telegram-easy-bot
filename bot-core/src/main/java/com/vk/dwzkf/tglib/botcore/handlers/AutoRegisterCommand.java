package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
public interface AutoRegisterCommand<T extends CommandHandler<? extends MessageContext>> {
    Command routingCommand();
    default Class<T> getHandler() {
        return (Class<T>) this.getClass();
    }
}
