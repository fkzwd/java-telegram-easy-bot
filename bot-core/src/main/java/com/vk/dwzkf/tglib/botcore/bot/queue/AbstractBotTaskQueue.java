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
 * @since 25.12.2024
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBotTaskQueue implements BotTaskQueue {
    protected final ArrayBlockingQueue<ExecutableTask<?>> queue = new ArrayBlockingQueue<>(1000);

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ExecutableTask<T> {
        final MessageTask<T> task;
        final CompletableFuture<T> result = new CompletableFuture<>();
    }

    protected abstract TelegramLongPollingBot getBot();

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void executeTask(ExecutableTask executableTask, int recursionLevel) {
        if (recursionLevel > 2) {
            executableTask.result.completeExceptionally(new IllegalStateException("Recursion level exceeded"));
            return;
        }
        try {
            Object sendMessage = executableTask.task.execute(getBot());
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
            handleApiException(executableTask, e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void handleApiException(ExecutableTask executableTask, TelegramApiException e) {
        BotCoreException botCoreException = new BotCoreException(
                "Unable to answer to chat %s".formatted(executableTask.task.getChatId()),
                e
        );
        log.error("Error executing telegram task", e);
        executableTask.result.completeExceptionally(botCoreException);
    }

    @Override
    public <T> T execute(MessageTask<T> messageTask) throws BotCoreException {
        try {
            return executeAsync(messageTask)
                    .get(
                            getSyncExecuteTimeout(),
                            getSyncExeuctionTimeUnit()
                    );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error while executing task {} {}", messageTask.getClass().getSimpleName(), messageTask, e);
            if (e.getCause() instanceof BotCoreException botCoreException) {
                throw botCoreException;
            }
            throw new RuntimeException(e);
        }
    }

    protected abstract TimeUnit getSyncExeuctionTimeUnit();

    protected abstract long getSyncExecuteTimeout();

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
}
