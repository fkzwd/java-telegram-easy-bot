package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.constants.StringConstants;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class NotFoundTextMessageHandler extends DefaultTextMessageHandler {
    @Override
    public boolean match(MessageContext messageContext) {
        return true;
    }

    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        messageContext.doAnswer(StringConstants.NOT_FOUND_MESSAGE);
    }
}
