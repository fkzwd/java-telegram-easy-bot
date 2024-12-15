package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.constants.StringConstants;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.CommandHandleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Slf4j
public class DefaultNotFoundCommandHandler implements CommandHandler<MessageContext> {
    /**
     * {@inheritDoc}
     * <pre>
     * Используется когда не найдено ни одного обработчика для команды
     * </pre>
     * @param messageContext контекст сообщения
     * @throws BotCoreException
     */
    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        try {
            messageContext.doAnswer(StringConstants.NOT_FOUND_MESSAGE);
        } catch (BotCoreException e) {
            throw new CommandHandleException(
                    String.format(
                            "Error handling command: %s",
                            messageContext.getCommandContext().getCommand()
                    ), e
            );
        }
    }

    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
