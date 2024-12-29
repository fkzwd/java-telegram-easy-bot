package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.concurrent.*;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@Slf4j
public class DefaultBotTaskQueue extends AbstractBotTaskQueue implements BotTaskQueue {
    public static final int SYNC_EXECUTION_TIMEOUT_RATE_MULTIPIER = 30;
    @Setter
    private ScheduledExecutorService taskExecutor = null;
    @Getter
    private final TelegramLongPollingBot bot;
    @Getter
    private final DefaultBotTaskQueueConfig defaultBotTaskQueueConfig;

    public DefaultBotTaskQueue(DefaultBotTaskQueueConfig defaultBotTaskQueueConfig, TelegramLongPollingBot bot) {
        this.bot = bot;
        this.defaultBotTaskQueueConfig = defaultBotTaskQueueConfig;
    }

    @Override
    public void init() {
        if (taskExecutor == null) {
            taskExecutor = new ScheduledThreadPoolExecutor(
                    1,
                    r -> {
                        Thread thread = new Thread(r);
                        thread.setName("DefaultBotTaskQueue [" + thread.getName() + "]");
                        thread.setDaemon(true);
                        return thread;
                    }
            );
        }
        taskExecutor.scheduleWithFixedDelay(
                this::executorLoop,
                0L,
                getDefaultBotTaskQueueConfig().getTaskExecutionRate(),
                getDefaultBotTaskQueueConfig().getTaskExecutionTimeUnit()
        );
    }

    @Override
    protected TimeUnit getSyncExeuctionTimeUnit() {
        return defaultBotTaskQueueConfig.getTaskExecutionTimeUnit();
    }

    @Override
    protected long getSyncExecuteTimeout() {
        return defaultBotTaskQueueConfig.getTaskExecutionRate() * SYNC_EXECUTION_TIMEOUT_RATE_MULTIPIER;
    }

    protected void executorLoop() {
        try {
            log.info("Current queue size: {}", queue.size());
            ExecutableTask<?> executableTask = queue.take();
            executeTask(executableTask, 0);
        } catch (Exception e) {
            log.error("Error on executing task", e);
//            throw new RuntimeException(e);
        }
    }
}
