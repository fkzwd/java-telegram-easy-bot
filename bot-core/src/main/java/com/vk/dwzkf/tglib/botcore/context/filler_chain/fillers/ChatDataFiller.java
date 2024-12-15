package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import com.vk.dwzkf.tglib.commons.enums.ChatType;
import com.vk.dwzkf.tglib.commons.utils.EnumHelper;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
public class ChatDataFiller extends DefaultContextFiller {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        String chatType = ctx.getChat().getType();
        ctx.setChatType(EnumHelper.getByName(ChatType.class, chatType));
        String chatId = ctx.getChat().getId().toString();
        ctx.setChatId(chatId);
        chain.fillNext(ctx);
    }
}
