package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
public class RawDataFiller extends DefaultContextFiller {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        Message messageUpdate = ctx.getUpdate().getMessage();
        if (messageUpdate != null) {
            String rawMessage = messageUpdate.getText();
            if (ctx.getRawData() == null) {
                ctx.setRawData(rawMessage);
            }
            chain.fillNext(ctx);
            return;
        }
        CallbackQuery callbackQuery = ctx.getUpdate().getCallbackQuery();
        if (callbackQuery != null) {
            String rawData = callbackQuery.getData();
            if (ctx.getRawData() == null) {
                ctx.setRawData(rawData);
            }
            chain.fillNext(ctx);
        }
    }
}
