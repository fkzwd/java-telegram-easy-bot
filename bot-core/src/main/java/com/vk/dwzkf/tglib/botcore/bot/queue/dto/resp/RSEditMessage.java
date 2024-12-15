package com.vk.dwzkf.tglib.botcore.bot.queue.dto.resp;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public final class RSEditMessage {
    private Message message;
    private boolean success;

    public boolean hasMessage() {
        return message != null;
    }
}
