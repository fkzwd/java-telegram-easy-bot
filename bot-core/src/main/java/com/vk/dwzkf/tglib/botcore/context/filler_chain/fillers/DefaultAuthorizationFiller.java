package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.cfg.BotConfiguration;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
@Component
@RequiredArgsConstructor
public class DefaultAuthorizationFiller extends DefaultContextFiller {
    private final BotConfiguration botConfig;

    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        ctx.setAuthorized(!botConfig.isSecure());
        chain.fillNext(ctx);
    }
}
