package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreRuntimeException;
import com.vk.dwzkf.tglib.botcore.exception.SupportedClassTypeMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultMessageHandler  {
    private final CommandHandlerRegistry commandHandlerRegistry;
    private final TextMessageHandlerRegistry textMessageHandlerRegistry;

    public void handleMessage(MessageContext messageContext) {
        if (!messageContext.isAuthorized()) {
            log.debug("Message {} skipped. unauthorized", messageContext);
            return;
        }
        try {
            handleInternal(messageContext);
        } catch (BotCoreException | BotCoreRuntimeException e) {
            try {
                messageContext.doAnswer("[ERROR]: "+e.getMessage());
            } catch (BotCoreException e2) {
                log.error("Error answering to user with error.", e2);
            }
            log.error("Error handling message. {}", messageContext, e);
        } catch (Exception e) {
            log.error("Unexpected error handling message. {}", messageContext, e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleInternal(MessageContext messageContext) throws BotCoreException {
        if (messageContext.hasCommand()) {
            CommandHandler handler = commandHandlerRegistry.getOrDefault(messageContext.getCommandContext().getCommand());
            if (handler.supports().isAssignableFrom(messageContext.getClass())) {
                handler.handle(messageContext);
            } else {
                throw new SupportedClassTypeMismatchException(
                        "Unable to handle command with bounded command handler.",
                        handler.getClass(),
                        handler.supports(),
                        messageContext.getClass()
                );
            }
            return;
        }
        textMessageHandlerRegistry.handleText(messageContext);
    }
}
