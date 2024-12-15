package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import com.vk.dwzkf.tglib.botcore.enums.InputFlag;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@Component
public class InputTypeFiller extends DefaultContextFiller {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        if (ctx.getRawData() != null && ctx.hasPhotos()) {
            ctx.addFlag(InputFlag.HAS_CAPTION);
        }
        if (ctx.isInlineAnswer()) {
            ctx.addFlag(InputFlag.INLINE_ANSWER);
        }
        if (ctx.hasPhotos()) {
            ctx.addFlag(InputFlag.HAS_PHOTO);
        }
        if (ctx.getRawData() != null) {
            ctx.addFlag(InputFlag.HAS_TEXT);
        }
        if (ctx.isHasCommand()) {
            ctx.addFlag(InputFlag.COMMAND);
        }
        chain.fillNext(ctx);
    }
}
