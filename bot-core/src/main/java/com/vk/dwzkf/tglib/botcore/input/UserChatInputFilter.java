package com.vk.dwzkf.tglib.botcore.input;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import lombok.RequiredArgsConstructor;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@RequiredArgsConstructor
public class UserChatInputFilter implements InputFilter {
    private final String chatId;
    private final String userId;

    @Override
    public boolean match(MessageContext inputContext) {
        return inputContext.getChatId().equals(chatId) && inputContext.getUserId().equals(userId);
    }
}
