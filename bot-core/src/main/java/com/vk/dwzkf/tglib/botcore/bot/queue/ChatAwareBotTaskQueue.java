package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.RateLimitConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.SmartBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.commons.enums.ChatType;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Roman Shageev
 * @since 25.12.2024
 */
@Slf4j
public class ChatAwareBotTaskQueue implements BotTaskQueue {
    private final Map<String, BotTaskQueue> queues = new ConcurrentHashMap<>();
    private final TelegramLongPollingBot bot;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);
    private final SmartBotTaskQueueConfig smartConfig;

    public ChatAwareBotTaskQueue(
            SmartBotTaskQueueConfig smartBotTaskQueueConfig,
            TelegramLongPollingBot bot
    ) {
        this.smartConfig = smartBotTaskQueueConfig;
        this.bot = bot;
    }

    @Override
    public <T> CompletableFuture<T> executeAsync(MessageTask<T> messageTask) {
        return getQueueForChat(messageTask).executeAsync(messageTask);
    }

    private <T> BotTaskQueue getQueueForChat(MessageTask<T> messageTask) {
        return queues.computeIfAbsent(messageTask.getChatId(), this::createChatTaskQueue);
    }

    private BotTaskQueue createChatTaskQueue(String chatId) {
        RateLimitConfig rateLimitConfig = resolveRateLimitConfig(chatId);
        SmartBotTaskQueue smartBotTaskQueue = new SmartBotTaskQueue(
                bot,
                smartConfig,
                rateLimitConfig
        );
        smartBotTaskQueue.setTaskExecutor(executorService);
        smartBotTaskQueue.init();
        return smartBotTaskQueue;
    }

    private RateLimitConfig resolveRateLimitConfig(String chatId) {
        boolean isPrivate = !chatId.startsWith("-");
        RateLimitConfig rateLimitConfig;

        if (isPrivate) {
            rateLimitConfig = smartConfig.getPrivateChatConfig();
        } else {
            rateLimitConfig = smartConfig.getGroupChatConfig();
        }

        rateLimitConfig = smartConfig.getDirectConfig()
                .getOrDefault(chatId, rateLimitConfig);

        if (rateLimitConfig == null) {
            rateLimitConfig = new RateLimitConfig(
                    smartConfig.getLimit(),
                    smartConfig.getUnit(),
                    smartConfig.getWindow()
            );
        }
        return rateLimitConfig;
    }

    @Override
    public <T> T execute(MessageTask<T> messageTask) throws BotCoreException {
        return getQueueForChat(messageTask).execute(messageTask);
    }

    @Override
    public void init() {

    }
}
