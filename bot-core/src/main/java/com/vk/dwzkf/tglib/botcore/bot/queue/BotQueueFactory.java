package com.vk.dwzkf.tglib.botcore.bot.queue;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * @author Roman Shageev
 * @since 30.12.2024
 */
public interface BotQueueFactory {
    BotTaskQueue create(TelegramLongPollingBot bot);
}
