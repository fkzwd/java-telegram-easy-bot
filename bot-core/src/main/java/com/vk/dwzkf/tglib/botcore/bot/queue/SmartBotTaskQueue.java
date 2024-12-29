package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.RateLimitConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.SmartBotTaskQueueConfig;
import com.vk.dwzkf.tglib.commons.utils.RateLimitCounter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Shageev
 * @since 25.12.2024
 */
@Slf4j
public class SmartBotTaskQueue extends DefaultBotTaskQueue {
    private final SmartBotTaskQueueConfig config;
    private final RateLimitCounter rateLimitCounter;

    @Getter
    private final TelegramLongPollingBot bot;

    public SmartBotTaskQueue(
            TelegramLongPollingBot bot,
            SmartBotTaskQueueConfig config
    ) {
        super(config.getDefaultBotTaskQueueConfig(), bot);
        this.bot = bot;
        this.config = config;
        this.rateLimitCounter = new RateLimitCounter(
                config.getLimit(),
                config.getUnit(),
                config.getWindow()
        );
    }

    public SmartBotTaskQueue(
            TelegramLongPollingBot bot,
            SmartBotTaskQueueConfig config,
            RateLimitConfig rateLimitConfig
    ) {
        super(config.getDefaultBotTaskQueueConfig(), bot);
        this.bot = bot;
        this.config = config;
        this.rateLimitCounter = new RateLimitCounter(
                rateLimitConfig.getLimit(),
                rateLimitConfig.getUnit(),
                rateLimitConfig.getWindow()
        );
    }

    private static final Pattern awaitPattern = Pattern.compile("^.*(?<err>Too many requests: retry after (?<duration>\\d+))", Pattern.CASE_INSENSITIVE);

    @Override
    protected void handleApiException(ExecutableTask executableTask, TelegramApiException e) {
        //Too Many Requests: retry after 33
        String message = e.getMessage();
        if (message != null && !message.isBlank()) {
            message = message.toLowerCase();
            Matcher matcher = awaitPattern.matcher(message);
            int secondsToAwait = Integer.parseInt(matcher.group("duration"));
            try {
                Thread.sleep(secondsToAwait * 1000L);
                queue.add(executableTask);
            } catch (InterruptedException ex) {
                log.error("Error waiting telegram timout of {} sec.", secondsToAwait);
                RuntimeException runtimeException = new RuntimeException(ex);
                runtimeException.addSuppressed(e);
                throw runtimeException;
            }
        } else {
            super.handleApiException(executableTask, e);
        }
    }

    @Override
    protected TimeUnit getSyncExeuctionTimeUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    protected long getSyncExecuteTimeout() {
        return 5L;
    }

    protected void executorLoop() {
        rateLimitCounter.execute(() -> {
            try {
                log.info("Current queue size: {}", queue.size());
                ExecutableTask<?> executableTask = queue.take();
                executeTask(executableTask, 0);
            } catch (Exception e) {
                log.error("Error on executing task", e);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        log.info("Initialized SMART bot task queue. Config: {}", config);
    }
}
