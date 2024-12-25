package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.SmartBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
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
    private final DefaultBotTaskQueueConfig config;
    private final TelegramLongPollingBot bot;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);
    private final SmartBotTaskQueueConfig smartConfig;

    public ChatAwareBotTaskQueue(
            SmartBotTaskQueueConfig smartBotTaskQueueConfig,
            DefaultBotTaskQueueConfig config,
            TelegramLongPollingBot bot
    ) {
        this.config = config;
        this.smartConfig = smartBotTaskQueueConfig;
        this.bot = bot;
    }

    @Override
    public <T> CompletableFuture<T> executeAsync(MessageTask<T> messageTask) {
        return getQueueForChat(messageTask).executeAsync(messageTask);
    }

    private <T> BotTaskQueue getQueueForChat(MessageTask<T> messageTask) {
        return queues.computeIfAbsent(messageTask.getChatId(), k -> {
            return createChatTaskQueue();
        });
    }

    private BotTaskQueue createChatTaskQueue() {
        SmartBotTaskQueue smartBotTaskQueue = new SmartBotTaskQueue(
                bot,
                smartConfig
        );
        smartBotTaskQueue.setTaskExecutor(executorService);
        smartBotTaskQueue.init();
        return smartBotTaskQueue;
    }

    @Override
    public <T> T execute(MessageTask<T> messageTask) throws BotCoreException {
        return getQueueForChat(messageTask).execute(messageTask);
    }

    @Override
    public void init() {

    }
}
