package com.vk.dwzkf.tglib.botcore.bot;

import com.vk.dwzkf.tglib.botcore.bot.queue.BotTaskQueue;
import com.vk.dwzkf.tglib.botcore.bot.queue.ChatAwareBotTaskQueue;
import com.vk.dwzkf.tglib.botcore.bot.queue.DefaultBotTaskQueue;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.DefaultBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.bot.queue.cfg.SmartBotTaskQueueConfig;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.MessageContextFactory;
import com.vk.dwzkf.tglib.botcore.context.MessageContextFiller;
import com.vk.dwzkf.tglib.botcore.handlers.DefaultMessageHandler;
import com.vk.dwzkf.tglib.commons.cfg.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@Slf4j
public class BotRunner extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final BotTaskQueue botTaskQueue;
    private final TelegramBotsApi botsApi;
    private final DefaultMessageHandler messageHandler;
    private final MessageContextFiller messageContextFiller;
    private final MessageContextFactory<? extends MessageContext> messageContextFactory;
    private final List<Executor> executors = Collections.synchronizedList(new ArrayList<>());

    public BotRunner(
            BotConfig botConfig,
            DefaultMessageHandler messageHandler,
            MessageContextFiller messageContextFiller,
            MessageContextFactory<? extends MessageContext> messageContextFactory,
            DefaultBotTaskQueueConfig defaultBotTaskQueueConfig,
            SmartBotTaskQueueConfig smartBotTaskQueueConfig
    ) throws Exception {
        super(botConfig.getToken());
        this.messageContextFactory = messageContextFactory;
        this.botsApi = new TelegramBotsApi(DefaultBotSession.class);
        this.botConfig = botConfig;
        this.messageHandler = messageHandler;
        this.messageContextFiller = messageContextFiller;
        this.botTaskQueue = new ChatAwareBotTaskQueue(smartBotTaskQueueConfig, defaultBotTaskQueueConfig, this);
        final int processors = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processors; i++) {
            executors.add(Executors.newSingleThreadExecutor());
        }
    }


    @EventListener(ApplicationStartedEvent.class)
    public void runBot() throws TelegramApiException {
        botsApi.registerBot(this);
        botTaskQueue.init();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received update: {}", update);
        MessageContext ctx = messageContextFactory.create();
        ctx.setBot(this);
        ctx.setTaskQueue(this.botTaskQueue);
        if (update.getMessage() != null) {
            Executor executor = resolveExecutor(update.getMessage().getChatId());
            executor.execute(() -> {
                boolean hasText = update.getMessage().getText() != null;
                boolean hasCaption = update.getMessage().getCaption() != null;
                hasCaption |= update.getMessage().getCaptionEntities() != null && !update.getMessage().getCaptionEntities().isEmpty();
                if (hasText || hasCaption) {
                    messageContextFiller.fillContext(ctx, update);
                    messageHandler.handleMessage(ctx);
                }
            });
        } else if (update.getCallbackQuery() != null) {
            Executor executor = resolveExecutor(update.getCallbackQuery().getMessage().getChatId());
            executor.execute(() -> {
                messageContextFiller.fillContext(ctx, update);
                messageHandler.handleMessage(ctx);
            });
        }
    }

    private Executor resolveExecutor(Long chatId) {
        long size = executors.size();
        long chat = Math.abs(chatId);
        int index = (int) (chat % size);
        return executors.get(index);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
