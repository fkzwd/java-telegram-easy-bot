package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
public class SimpleMessageFiller extends DefaultContextFiller {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        if (!ctx.hasCommand()) {
            ctx.setMessage(ctx.getRawData());
            chain.fillNext(ctx);
            return;
        }
        if (ctx.getCommandContext().isHasArgs()) {
            ctx.setMessage(ctx.getRawData().substring(ctx.getCommandEndIdx()+1));
        } else {
            ctx.setMessage("");
        }
        chain.fillNext(ctx);
    }
}
