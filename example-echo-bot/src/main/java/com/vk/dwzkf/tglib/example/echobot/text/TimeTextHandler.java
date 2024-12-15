package com.vk.dwzkf.tglib.example.echobot.text;

import com.vk.dwzkf.tglib.botcore.annotations.TextHandler;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.handler.FormSender;
import com.vk.dwzkf.tglib.botcore.handlers.TextMessageHandler;
import com.vk.dwzkf.tglib.example.echobot.forms.CurrentTimeForm;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author Roman Shageev
 * @since 16.12.2024
 */
@Service
@TextHandler
@Order(0)
public class TimeTextHandler extends FormSender<MessageContext> implements TextMessageHandler<MessageContext> {
    @Override
    public boolean match(MessageContext messageContext) {
        return "time".equalsIgnoreCase(messageContext.getRawData());
    }

    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        Form form = new CurrentTimeForm();
        sendForm(form, messageContext);
    }

    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
