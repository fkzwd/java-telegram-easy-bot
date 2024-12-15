package com.vk.dwzkf.tglib.botcore.service;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.FormRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@Component
@RequiredArgsConstructor
public class FormSenderService {
    public <T extends MessageContext> void sendForm(
            Form form, T messageContext,
            FormRenderer formRenderer,
            UserFormService userFormService,
            UserFormService.FormAccess formAccess
    ) throws BotCoreException {
        form.preRender();
        InlineKeyboardMarkup keyboardMarkup = formRenderer.render(form);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(formRenderer.renderMessage(form));
        sendMessage.setReplyMarkup(keyboardMarkup);
        final String chatId = messageContext.getChatId();
        final String userId = messageContext.getUserId();
        Message response = messageContext.doAnswer(sendMessage, chatId);
        String messageId = response.getMessageId().toString();
        userFormService.setUserForm(chatId, userId, messageId, form, formAccess);
    }
}
