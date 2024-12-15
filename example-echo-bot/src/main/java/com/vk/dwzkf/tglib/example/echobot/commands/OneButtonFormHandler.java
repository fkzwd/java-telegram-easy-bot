package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.botcore.forms.handler.FormSender;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;
import com.vk.dwzkf.tglib.example.echobot.forms.CurrentTimeForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author Roman Shageev
 * @since 15.12.2024
 */
@Component
@RouteCommand(command = "time")
public class OneButtonFormHandler extends FormSender<MessageContext> implements CommandHandler<MessageContext> {

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
