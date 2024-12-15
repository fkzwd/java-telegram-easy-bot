package com.vk.dwzkf.tglib.botcore.bot.queue.tasks;

import com.vk.dwzkf.tglib.botcore.bot.queue.MessageTask;
import lombok.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@Getter
public class SendMessageTask implements MessageTask<Message> {
    private String chatId;
    @Setter
    private String message;
    @Setter
    private SendMessage preparedMessage;

    public SendMessageTask(String chatId) {
        this.chatId = chatId;
    }

    public SendMessageTask(String message, String chatId) {
        this.message = message;
        this.chatId = chatId;
    }

    public SendMessageTask(String chatId, SendMessage sendMessage) {
        this.chatId = chatId;
        this.preparedMessage = sendMessage;
    }

    @Override
    public void onChatIdChanged(Long newChatId) {
        this.chatId = newChatId.toString();
    }

    @Override
    public Message execute(TelegramLongPollingBot bot) throws TelegramApiException {
        SendMessage sendMessage = preparedMessage == null ? new SendMessage() : preparedMessage;
        sendMessage.setChatId(chatId);
        if (preparedMessage == null) {
            sendMessage.setText(message);
        }
        sendMessage.setParseMode(ParseMode.HTML);
        return bot.execute(sendMessage);
    }
}
