package com.vk.dwzkf.tglib.botcore.forms.handler;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.forms.actions.ActionType;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.FormRenderer;
import com.vk.dwzkf.tglib.botcore.handlers.AutoRegisterCommand;
import com.vk.dwzkf.tglib.botcore.handlers.Command;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;
import com.vk.dwzkf.tglib.botcore.service.UserFormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 11.11.2023
 */
@ConditionalOnProperty(prefix = "bot.form-handler", name = "enabled", havingValue = "true", matchIfMissing = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class FormCommandHandler implements CommandHandler<MessageContext>, AutoRegisterCommand<FormCommandHandler> {
    private final FormRenderer formRenderer;
    private final UserFormService userFormService;
    private final FormHandlerConfig formHandlerConfig;

    @Override
    public Command routingCommand() {
        return formHandlerConfig.getDispatchCommand();
    }

    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        boolean inlineAnswer = messageContext.isInlineAnswer();
        if (!inlineAnswer) throw new BotCoreException("Only inline answer supported");
        if (!messageContext.getCommandContext().hasArgs()) throw new BotCoreException("No args found");
        if (messageContext.getCommandContext().getCommandArgs().length != 2) {
            throw new BotCoreException("Required 2 args not found");
        }
        String[] args = messageContext.getCommandContext().getCommandArgs();
        Integer rowIndex = Integer.parseInt(args[0]);
        Integer buttonIndex = Integer.parseInt(args[1]);

        String chatId = messageContext.getChatId();
        Integer messageId = messageContext.getInlineMessage().getMessageId();
        String userId = messageContext.getUserId();
        String messageIdStr = messageId.toString();
        Form form = null;
        try {
            form = userFormService.findForm(messageContext, chatId, userId, messageIdStr);
        } catch (UserFormService.NotYourFormException e) {
            logIllegalAccess(messageContext, e);
            return;
        }
        if (form == null) {
            updateInlineMessage(
                    messageContext,
                    chatId,
                    messageId,
                    null,
                    "Форма неактуальна"
            );
            return;
        }
        List<AfterClickAction<?>> actions = form.onButtonClick(rowIndex, buttonIndex, messageContext);
        for (AfterClickAction<?> action : actions) {
            if (action.is(ActionType.UPDATE_FORM)) {
                AfterClickAction<Form> updateForm = action.as(ActionType.UPDATE_FORM);
                Form nextForm = updateForm.getObject();
                handleNextForm(messageContext, chatId, messageId, userId, messageIdStr, nextForm);
            } else if (action.is(ActionType.ANSWER_TEXT)) {
                messageContext.doAnswer(
                        action.as(ActionType.ANSWER_TEXT)
                                .getObject(),
                        messageContext.getChatId()
                );
            } else if (action.is(ActionType.DELETE_MESSAGE)) {
                try {
                    userFormService.remove(messageContext, chatId, userId, messageIdStr);
                } catch (UserFormService.NotYourFormException e) {
                    logIllegalAccess(messageContext, e);
                    return;
                }
                messageContext.doDelete(messageId, chatId);
            }
        }
    }

    //TODO: refactor
    public void handleNextForm(MessageContext messageContext, String chatId, Integer messageId, String userId, String messageIdStr, Form nextForm) throws BotCoreException {
        if (nextForm == null) {
            try {
                userFormService.remove(messageContext, chatId, userId, messageIdStr);
            } catch (UserFormService.NotYourFormException e) {
                logIllegalAccess(messageContext, e);
                return;
            }
            updateInlineMessage(
                    messageContext,
                    chatId,
                    messageId,
                    null,
                    "Форма закрыта"
            );
            return;
        }
        nextForm.preRender();
        if (nextForm.hasButtons()) {
            userFormService.setUserForm(chatId, userId, messageIdStr, nextForm);
        } else {
            try {
                userFormService.remove(messageContext, chatId, userId, messageIdStr);
            } catch (UserFormService.NotYourFormException e) {
                logIllegalAccess(messageContext, e);
                return;
            }
        }
        InlineKeyboardMarkup reply = formRenderer.render(nextForm);
        updateInlineMessage(
                messageContext,
                chatId,
                messageId,
                reply,
                formRenderer.renderMessage(nextForm)
        );
    }

    public void logIllegalAccess(MessageContext messageContext, UserFormService.NotYourFormException e) throws BotCoreException {
        if (log.isDebugEnabled()) {
            log.warn("Illegall access to form. Message: {}", e.getMessage(), e);
        } else {
            log.warn("Illegall access to form. Message: {}", e.getMessage());
        }
        messageContext.doAnswer(
                String.format(
                        "<b>%s</b>, ай-ай-ай, <i>нельзя редактировать чужие формы!</i>",
                        messageContext.getUserName()
                ), messageContext.getChatId()
        );
    }

    private void updateInlineMessage(MessageContext messageContext, String chatId, Integer messageId, InlineKeyboardMarkup replyMarkup, String messageText) throws BotCoreException {
        EditMessageText editMessageText = new EditMessageText(messageText);
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setReplyMarkup(replyMarkup);
        messageContext.doUpdate(editMessageText, chatId);
    }

    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
