package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import com.vk.dwzkf.tglib.commons.cfg.BotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@RequiredArgsConstructor
public class BotNameFiller extends DefaultContextFiller {
    private final BotConfig botConfig;

    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        ctx.setThisBotName(botConfig.getBotName());
        chain.fillNext(ctx);
    }
}
