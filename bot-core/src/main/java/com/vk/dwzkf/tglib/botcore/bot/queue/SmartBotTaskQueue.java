package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.SmartBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Shageev
 * @since 25.12.2024
 */
@Slf4j
public class SmartBotTaskQueue extends DefaultBotTaskQueue {
    private static final AtomicInteger counter = new AtomicInteger(0);
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

    private static final Pattern awaitPatter = Pattern.compile("^.*(?<err>Too many requests: retry after (?<duration>\\d+))", Pattern.CASE_INSENSITIVE);
    @Override
    protected void handleApiException(ExecutableTask executableTask, TelegramApiException e) {
        //Too Many Requests: retry after 33
        String message = e.getMessage();
        if (message != null && !message.isBlank()) {
            message = message.toLowerCase();
            Matcher matcher = awaitPatter.matcher(message);
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
        try {
            if (rateLimitCounter.wouldLimitExceed()) {
                rateLimitCounter.await();
            }
            log.info("Current queue size: {}", queue.size());
            ExecutableTask<?> executableTask = queue.take();
            executeTask(executableTask, 0);
            rateLimitCounter.inc();
        } catch (Exception e) {
            log.error("Error on executing task", e);
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        super.init();
        log.info("Initialized SMART bot task queue. Config: {}", config);
    }
}
