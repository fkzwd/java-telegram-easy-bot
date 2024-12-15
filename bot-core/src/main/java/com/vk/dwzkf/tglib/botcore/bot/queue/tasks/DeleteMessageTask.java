package com.vk.dwzkf.tglib.botcore.bot.queue.tasks;

import com.vk.dwzkf.tglib.botcore.bot.queue.MessageTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@AllArgsConstructor
@Getter
public class DeleteMessageTask implements MessageTask<Boolean> {
    private String messageId;
    private String chatId;

    @Override
    public void onChatIdChanged(Long newChatId) {
        this.chatId = newChatId.toString();
    }

    @Override
    public Boolean execute(TelegramLongPollingBot bot) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, Integer.parseInt(messageId));
        return bot.execute(deleteMessage);
    }
}
