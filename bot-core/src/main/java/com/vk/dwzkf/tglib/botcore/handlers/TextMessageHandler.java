package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;

/**
 * Интерфейс обработчика текстовых сообщений бота
 * @author Roman Shageev
 * @since 11.10.2023
 */
public interface TextMessageHandler<T extends MessageContext> {
    /**
     * <pre>
     *      Подходит ли данный обработчик для обработки полученного сообщения
     *
     *      Если возвращает <code>false</code>, то сообщение будет
     *      обработано <b>следующим в очереди</b> обработчиком
     * </pre>
     * @param messageContext контекст сообщения
     * @return подходит или нет
     */
    boolean match(T messageContext);

    /**
     * Вызывается в случае если {@link #match(MessageContext)} вернул {@code true}
     * @param messageContext контекст сообщения
     * @throws BotCoreException
     */
    void handle(T messageContext) throws BotCoreException;
    Class<T> supports();
}
