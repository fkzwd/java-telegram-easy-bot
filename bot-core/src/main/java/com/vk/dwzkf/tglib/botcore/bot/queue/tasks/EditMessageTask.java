package com.vk.dwzkf.tglib.botcore.bot.queue.tasks;

import com.vk.dwzkf.tglib.botcore.bot.queue.MessageTask;
import com.vk.dwzkf.tglib.botcore.bot.queue.dto.resp.RSEditMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@AllArgsConstructor
@Getter
public class EditMessageTask implements MessageTask<RSEditMessage> {
    private EditMessageText editMessageText;
    private String chatId;

    @Override
    public void onChatIdChanged(Long newChatId) {
        editMessageText.setChatId(newChatId);
    }

    @Override
    public RSEditMessage execute(TelegramLongPollingBot bot) throws TelegramApiException {
        editMessageText.setChatId(chatId);
        editMessageText.setParseMode(ParseMode.HTML);
        Object execute = bot.execute(editMessageText);
        RSEditMessage response = new RSEditMessage();
        if (execute instanceof Message res) {
            response.setMessage(res);
            response.setSuccess(true);
            return response;
        }
        if (execute instanceof Boolean res) {
            response.setSuccess(res);
            return response;
        }

        // what's going on ??
        throw new IllegalStateException("Unable to edit message?");
    }
}
