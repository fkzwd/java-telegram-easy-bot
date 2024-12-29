package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.BotQueueFactoryConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.SmartBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.enums.BotQueueType;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * @author Roman Shageev
 * @since 30.12.2024
 */
@RequiredArgsConstructor
@Service
public class DefaultBotQueueFactory implements BotQueueFactory {
    private final SmartBotTaskQueueConfig smartBotTaskQueueConfig;
    private final DefaultBotTaskQueueConfig defaultBotTaskQueueConfig;
    private final BotQueueFactoryConfig config;

    @Override
    public BotTaskQueue create(TelegramLongPollingBot bot) {
        if (config.getType() == BotQueueType.SMART) {
            return new ChatAwareBotTaskQueue(smartBotTaskQueueConfig, bot);
        }
        return new DefaultBotTaskQueue(defaultBotTaskQueueConfig, bot);
    }
}
