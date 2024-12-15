package com.vk.dwzkf.tglib.botcore.context;

import com.vk.dwzkf.tglib.botcore.bot.queue.BotTaskQueue;
import com.vk.dwzkf.tglib.botcore.bot.queue.MessageTask;
import com.vk.dwzkf.tglib.botcore.bot.queue.dto.resp.RSEditMessage;
import com.vk.dwzkf.tglib.botcore.bot.queue.tasks.DeleteMessageTask;
import com.vk.dwzkf.tglib.botcore.bot.queue.tasks.EditMessageTask;
import com.vk.dwzkf.tglib.botcore.bot.queue.tasks.SendMessageTask;
import com.vk.dwzkf.tglib.botcore.context.photos.MessagePhoto;
import com.vk.dwzkf.tglib.botcore.enums.InputFlag;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.NotFoundException;
import com.vk.dwzkf.tglib.commons.enums.ChatType;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageContext {
    //TODO: add update type flag
    @ToString.Exclude
    private final Logger log = LoggerFactory.getLogger(getClass());
    private BotTaskQueue taskQueue;

    @ToString.Exclude
    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private ChatType chatType;
    @ToString.Exclude
    private Update update;
    /**
     * Just raw message
     */
    protected String rawData;
    /**
     * Message itself if not command or all after /command and space or empty string if no args
     */
    protected String message;
    protected int commandEndIdx = -1;
    protected boolean hasCommand = false;
    protected String userId;
    protected String userName;
    protected String userLogin;
    protected boolean authorized = false;
    @Getter
    private CommandContext commandContext = new CommandContext();
    protected String thisBotName;
    protected String chatId;
    protected List<MessagePhoto> photos = new ArrayList<>();
    protected List<InputFlag> inputFlags = new ArrayList<>();

    public void addFlag(InputFlag flag) {
        if (!inputFlags.contains(flag)) {
            inputFlags.add(flag);
        }
    }

    public InputFlag[] inputFlags() {
        return inputFlags.toArray(InputFlag[]::new);
    }

    /**
     * unsafe usage
     */
    protected TelegramLongPollingBot bot;

    public final boolean hasCommand() {
        return hasCommand;
    }

    public final boolean hasPhotos() {
        return !photos.isEmpty();
    }

    public void addPhoto(MessagePhoto photo) {
        photos.add(photo);
    }

    public final void setAttribute(String key, Object value) {
        addAttribute(key, value, false);
    }

    public final Object getAttribute(String key) {
        return attributes.get(key);
    }

    public final void addAttribute(String key, Object value, boolean ifNotExists) {
        if (attributes.containsKey(key) && ifNotExists) {
            throw new IllegalStateException(
                    String.format(
                            "Unable to add attribute %s for key %s cause already exists: %s",
                            value,
                            key,
                            attributes.get(key)
                    )
            );
        }
        attributes.put(key, value);
    }

    public void doAnswer(String message) throws BotCoreException {
        doAnswer(message, chatId);
    }

    public Message doAnswer(String message, String chatId) throws BotCoreException {
        return taskQueue.execute(new SendMessageTask(message, chatId));
    }

    public Message doAnswer(SendMessage sendMessage, String chatId) throws BotCoreException {
        SendMessageTask task = new SendMessageTask(chatId, sendMessage);
        return taskQueue.execute(task);
    }

    public RSEditMessage doUpdate(EditMessageText editMessage, String chatId) throws BotCoreException {
        EditMessageTask editMessageTask = new EditMessageTask(editMessage, chatId);
        return taskQueue.execute(editMessageTask);
    }

    public boolean doDelete(int messageId, String chatId) throws BotCoreException {
        return taskQueue.execute(new DeleteMessageTask(messageId+"", chatId));
    }

    public boolean isInlineAnswer() {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        return callbackQuery != null;
    }

    public Message getInlineMessage() {
        if (!isInlineAnswer()) {
            throw new NotFoundException("Not found inline message");
        }
        CallbackQuery callbackQuery = update.getCallbackQuery();
        return getCallbackQueryMessage(callbackQuery);
    }

    private static Message getCallbackQueryMessage(CallbackQuery callbackQuery) {
        if (callbackQuery.getMessage() instanceof Message) {
            return (Message) callbackQuery.getMessage();
        } else {
            throw new NotFoundException("Not found inline message");
        }
    }

    public Chat getChat() {
        Message message = update.getMessage();
        if (message != null) {
            return message.getChat();
        }
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery != null) {
            return getCallbackQueryMessage(callbackQuery).getChat();
        }
        throw new NotFoundException("Not found chat in update");
    }

    public User getFrom() {
        Message message = update.getMessage();
        if (message != null) {
            User from = message.getFrom();
            if (from == null) {
                throw new NotFoundException("Not found from");
            }
            return from;
        }
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery != null) {
            return callbackQuery.getFrom();
        }
        throw new NotFoundException("Not found from");
    }

    public Message doAnswer(SendAnimation sendAnimation, String chatId) throws BotCoreException {
        //TODO:
        return taskQueue.execute(new MessageTask<>() {
            private String chat = chatId;

            @Override
            public String getChatId() {
                return chat;
            }

            @Override
            public void onChatIdChanged(Long newChatId) {
                this.chat = chatId;
            }

            @Override
            public Message execute(TelegramLongPollingBot bot) throws TelegramApiException {
                sendAnimation.setChatId(chat);
                return bot.execute(sendAnimation);
            }
        });
    }
}
