package com.vk.dwzkf.tglib.botcore.bot.queue;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
public interface MessageTask<T> {
    String getChatId();
    void onChatIdChanged(Long newChatId);
    T execute(TelegramLongPollingBot bot) throws TelegramApiException;
}
