package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;

/**
 * Обработчик команд
 * @author Roman Shageev
 * @since 11.10.2023
 */
public interface CommandHandler<T extends MessageContext> {
    /**
     * <pre>
     * Вызывается при получении сообщений вида <code>/command args</code>
     * </pre>
     * @param messageContext контекст сообщения
     * @throws BotCoreException
     */
    void handle(T messageContext) throws BotCoreException;

    /**
     * <pre>
     *      Имплементации должны возвращать класс <code>T</code>
     * </pre>
     * @return
     */
    Class<T> supports();

    default String getName() {
        return this.getClass().getName();
    }
}
