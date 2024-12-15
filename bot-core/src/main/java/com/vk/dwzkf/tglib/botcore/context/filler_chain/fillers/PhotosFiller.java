package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.ContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import com.vk.dwzkf.tglib.botcore.context.photos.DefaultMessagePhoto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Comparator;
import java.util.List;

/**
 * @author Roman Shageev
 * @since 30.11.2023
 */
@Component
public class PhotosFiller implements ContextFiller<MessageContext> {
    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        fill(ctx);
        chain.fillNext(ctx);
    }

    public void fill(MessageContext ctx) {
        Message message = ctx.getUpdate().getMessage();
        if (message == null) {
            return;
        }
        List<PhotoSize> photos = message.getPhoto();
        if (photos == null || photos.isEmpty()) {
            return;
        }
        photos.sort(
                Comparator.comparing(PhotoSize::getFileId)
                        .thenComparing(PhotoSize::getWidth)
        );
        ctx.addPhoto(new DefaultMessagePhoto<>(ctx.getBot(), photos));
    }

    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
