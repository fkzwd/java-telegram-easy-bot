package com.vk.dwzkf.tglib.botcore.service;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roman Shageev
 * @since 14.11.2023
 */
@Component
public class UserFormService {
    private final Map<String, Map<String, FormBind>> forms = new ConcurrentHashMap<>();

    public static class NotYourFormException extends Exception {
        public NotYourFormException() {
        }

        public NotYourFormException(String message) {
            super(message);
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static final class FormBind {
        Form form;
        String creatorUserId;
        FormAccess formAccess;

        public FormBind(Form form, String creatorUserId) {
            this.form = form;
            this.creatorUserId = creatorUserId;
        }
    }


    public interface FormAccess {
        boolean hasAccess(MessageContext ctx, FormBind formBind, String chatId, String userId, String messageId);
    }
    public static final FormAccess DEFAULT_ACCESS = (ctx, formBind, chatId, userId, messageId) ->
            formBind.getCreatorUserId().equals(userId);

    public Map<String, FormBind> getUserChatForms(String chatId) {
        return forms.computeIfAbsent(chatId, c -> new ConcurrentHashMap<>());
    }

    public Form findForm(
            MessageContext initiator,
            String chatId,
            String userId,
            String messageId
    ) throws NotYourFormException {
        FormBind formBind = getUserChatForms(chatId).get(messageId);
        if (formBind == null) return null;
        boolean access = formBind.getFormAccess()
                .hasAccess(initiator, formBind, chatId, userId, messageId);
        if (!access) {
            throw new NotYourFormException(notYourFormMessage(userId, formBind));
        }
        return formBind.getForm();
    }

    public void setUserForm(String chatId, String userId, String messageId, Form form) {
        setUserForm(chatId, userId, messageId, form, DEFAULT_ACCESS);
    }

    public void setUserForm(String chatId, String userId, String messageId, Form form, FormAccess formAccess) {
        FormBind formBind = getUserChatForms(chatId).get(messageId);
        if (formBind == null)
            getUserChatForms(chatId).put(messageId, new FormBind(form, userId, formAccess));
        else
            formBind.setForm(form);
    }


    public void remove(MessageContext messageContext, String chatId, String userId, String messageId)  throws NotYourFormException {
        Map<String, FormBind> message2form = getUserChatForms(chatId);
        FormBind formBind = message2form.get(messageId);
        boolean access = formBind.getFormAccess().hasAccess(messageContext, formBind, chatId, userId, messageId);
        if (!access) {
            throw new NotYourFormException(notYourFormMessage(userId, formBind));
        }
        message2form.remove(messageId);
    }

    public String notYourFormMessage(String userId, FormBind formBind) {
        return String.format(
                "Illegal acces; Form bound to user %s but received %s",
                formBind.getCreatorUserId(),
                userId
        );
    }
}
