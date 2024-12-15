package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@Component
public class CaptionsFiller extends DefaultContextFiller {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        fill(ctx);
        chain.fillNext(ctx);
    }

    private void fill(MessageContext ctx) {
        Message message = ctx.getUpdate().getMessage();
        if (message == null) {
            return;
        }
        if (message.getCaptionEntities() == null && message.getCaption() == null) {
            return;
        }
        if (message.getCaption() != null) {
            ctx.setRawData(message.getCaption());
            return;
        }
        if (!(message.getCaptionEntities() != null && !message.getCaptionEntities().isEmpty())) {
            return;
        }
        String msg = message.getCaptionEntities().stream()
                .filter(e -> e.getText() != null)
                .map(e -> e.getText())
                .collect(Collectors.joining("\n"));
        ctx.setRawData(msg);
    }
}
