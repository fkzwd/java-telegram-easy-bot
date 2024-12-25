package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Roman Shageev
 * @since 25.12.2024
 */
@RequiredArgsConstructor
@Slf4j
public class ChatAwareBotTaskQueue implements BotTaskQueue {
    private final Map<String, DefaultBotTaskQueue> queues = new ConcurrentHashMap<>();
    private final DefaultBotTaskQueueConfig config;
    private final TelegramLongPollingBot bot;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);

    @Override
    public <T> CompletableFuture<T> executeAsync(MessageTask<T> messageTask) {
        return getQueueForChat(messageTask).executeAsync(messageTask);
    }

    private <T> DefaultBotTaskQueue getQueueForChat(MessageTask<T> messageTask) {
        return queues.computeIfAbsent(messageTask.getChatId(), k -> {
            DefaultBotTaskQueue defaultBotTaskQueue = new DefaultBotTaskQueue(config, bot);
            defaultBotTaskQueue.setTaskExecutor(executorService);
            defaultBotTaskQueue.init();
            return defaultBotTaskQueue;
        });
    }

    @Override
    public <T> T execute(MessageTask<T> messageTask) throws BotCoreException {
        return getQueueForChat(messageTask).execute(messageTask);
    }

    @Override
    public void init() {

    }
}
