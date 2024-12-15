package com.vk.dwzkf.tglib.botcore.bot.queue;

import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
public interface BotTaskQueue {
    <T> CompletableFuture<T> executeAsync(MessageTask<T> messageTask);
    <T> T execute(MessageTask<T> messageTask) throws BotCoreException;
    void init();
}
