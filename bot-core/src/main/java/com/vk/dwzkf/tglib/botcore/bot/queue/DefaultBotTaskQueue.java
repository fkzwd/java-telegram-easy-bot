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
@RequiredArgsConstructor
public class DefaultBotTaskQueue implements BotTaskQueue {
    private static final long SYNC_EXECUTION_TIMEOUT = 5;
    private static final TimeUnit SYNC_EXECUTION_TIMEOUT_UNIT = TimeUnit.SECONDS;
    //TODO: сделать возможность конфигурировать эти параметры
    public static final long INITIAL_DELAY = 0L;
    public static final int SYNC_EXECUTION_TIMEOUT_RATE_MULTIPIER = 30;
    private final DefaultBotTaskQueueConfig defaultBotTaskQueueConfig;

    private final BlockingQueue<ExecutableTask<?>> queue = new ArrayBlockingQueue<>(1000);
    @Setter
    private ScheduledExecutorService taskExecutor = null;

    private final TelegramLongPollingBot bot;

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ExecutableTask<T> {
        final MessageTask<T> task;
        final CompletableFuture<T> result = new CompletableFuture<>();
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
        taskExecutor.scheduleAtFixedRate(
                this::executorLoop,
                INITIAL_DELAY,
                defaultBotTaskQueueConfig.getTaskExecutionRate(),
                defaultBotTaskQueueConfig.getTaskExecutionTimeUnit()
        );
    }

    private void executorLoop() {
        try {
            log.info("Current queue size: {}", queue.size());
            ExecutableTask<?> executableTask = queue.take();
            executeTask(executableTask, 0);
        } catch (Exception e) {
            log.error("Error on executing task", e);
//            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void executeTask(ExecutableTask executableTask, int recursionLevel) {
        if (recursionLevel > 2) {
            executableTask.result.completeExceptionally(new IllegalStateException("Recursion level exceeded"));
            return;
        }
        try {
            Object sendMessage = executableTask.task.execute(bot);
            executableTask.result.complete(sendMessage);
        } catch (TelegramApiRequestException e) {
            log.error("Error executing telegram task", e);
            ResponseParameters responseParameters = e.getParameters();
            if (responseParameters == null) {
                executableTask.result.completeExceptionally(e);
                return;
            }
            //if chat id was changed to new chat id, then update in database and try to send again
            Long migrateToChatId = responseParameters.getMigrateToChatId();
            if (migrateToChatId == null) {
                executableTask.result.completeExceptionally(e);
                return;
            }
            executableTask.task.onChatIdChanged(migrateToChatId);
            executeTask(executableTask, recursionLevel + 1);
        } catch (TelegramApiException e) {
            BotCoreException botCoreException = new BotCoreException(
                    "Unable to answer to chat %s".formatted(executableTask.task.getChatId()),
                    e
            );
            log.error("Error executing telegram task", e);
            executableTask.result.completeExceptionally(botCoreException);
        }
    }

    @Override
    public <T> CompletableFuture<T> executeAsync(MessageTask<T> messageTask) {
        ExecutableTask<T> executableTask = new ExecutableTask<T>(messageTask);
        try {
            queue.put(executableTask);
        } catch (InterruptedException e) {
            executableTask.getResult().completeExceptionally(e);
        }
        return executableTask.result;
    }

    @Override
    public <T> T execute(MessageTask<T> messageTask) throws BotCoreException {
        try {
            return executeAsync(messageTask)
                    .get(
                            defaultBotTaskQueueConfig.getTaskExecutionRate() * SYNC_EXECUTION_TIMEOUT_RATE_MULTIPIER,
                            defaultBotTaskQueueConfig.getTaskExecutionTimeUnit()
                    );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error while executing task {} {}", messageTask.getClass().getSimpleName(), messageTask, e);
            if (e.getCause() instanceof BotCoreException botCoreException) {
                throw botCoreException;
            }
            throw new RuntimeException(e);
        }
    }
}
