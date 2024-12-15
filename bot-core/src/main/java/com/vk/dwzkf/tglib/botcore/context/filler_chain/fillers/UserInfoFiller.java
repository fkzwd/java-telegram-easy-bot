package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
public class UserInfoFiller extends DefaultContextFiller {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        Long id = ctx.getFrom().getId();
        ctx.setUserId(id.toString());
        ctx.setUserLogin(ctx.getFrom().getUserName());
        ctx.setUserName(ctx.getFrom().getFirstName());
        chain.fillNext(ctx);
    }
}
