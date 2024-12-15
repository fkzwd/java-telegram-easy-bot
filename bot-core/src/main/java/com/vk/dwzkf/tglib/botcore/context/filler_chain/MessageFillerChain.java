package com.vk.dwzkf.tglib.botcore.context.filler_chain;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@RequiredArgsConstructor
@Slf4j
public class MessageFillerChain {
    @Setter
    private List<ContextFiller<? extends MessageContext>> fillers = null;
    private int currentFiller = 0;

    @SuppressWarnings("rawtypes")
    public void fillContext(MessageContext ctx) {
        if (fillers.size() == 0) return;
        ContextFiller filler = fillers.get(currentFiller++);
        fill(ctx, filler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fill(MessageContext ctx, ContextFiller filler) {
        Class<?> supports = filler.supports();
        if (supports.isAssignableFrom(ctx.getClass())) {
            filler.fill(ctx, this);
        } else {
            throw new IllegalStateException(
                    String.format(
                            "Unable to fill message context of class %s with filler that supports %s. \n" +
                            "Try to override MessageContextCreator bean",
                            ctx.getClass(),
                            filler.supports()
                    )
            );
        }
    }

    public void fillNext(MessageContext messageContext) {
        if (fillers.size() - 1 < currentFiller) return;
        fill(messageContext, fillers.get(currentFiller++));
    }
}
