package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.handler.FormSender;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;
import com.vk.dwzkf.tglib.botcore.input.InputWaiterService;
import com.vk.dwzkf.tglib.example.echobot.forms.simple_form.IncrementButton;
import com.vk.dwzkf.tglib.example.echobot.forms.simple_form.NumberInputButton;
import com.vk.dwzkf.tglib.example.echobot.forms.simple_form.RefreshButton;
import com.vk.dwzkf.tglib.example.echobot.forms.simple_form.TextInputButton;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
@Component
@RequiredArgsConstructor
@RouteCommand(command = "simpleform", description = "Description")
public class SimpleFormHandler extends FormSender<MessageContext> implements CommandHandler<MessageContext> {
    private static final String[] formats = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss",
            "HH:mm:ss",
            "yyyy-MM-dd"
    };
    private static final DateTimeFormatter[] formatters = Arrays.stream(formats)
            .map(DateTimeFormatter::ofPattern)
            .toArray(DateTimeFormatter[]::new);

    private final InputWaiterService inputWaiterService;

    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        Form form = new Form();
        AtomicInteger formatIndex = new AtomicInteger(0);
        StringBuilder text = new StringBuilder();
        AtomicReference<Integer> choosenNumber = new AtomicReference<>(null);
        form.putAttribute("fmt_idx", formatIndex);
        form.setTextProvider(() -> renderFormText(form, choosenNumber, text));
        form.setPreRender(() -> {
            form.clear();
            form.addRow().addButton(new RefreshButton(form));
            form.addRow().addButton(
                    new IncrementButton(
                            "Следующий формат даты",
                            form,
                            formatters.length, formatIndex
                    )
            );
            form.addRow().addButton(new TextInputButton(text, inputWaiterService, form));
            form.addRow().addButton(new NumberInputButton(form, choosenNumber));
            form.createControls(true, true);
        });
        sendForm(form, messageContext);
    }

    private static String renderFormText(Form form, AtomicReference<Integer> choosenNumber, StringBuilder text) {
        AtomicInteger atomicInteger = form.getAttribute("fmt_idx");
        DateTimeFormatter formatter = formatters[atomicInteger.get()];
        return """
                Формат: %s
                Значение: %s
                ---контекст---
                Выбранное число: %s
                Введенный текст: %s
                """
                .formatted(
                        formats[atomicInteger.get()],
                        LocalDateTime.now().format(formatter),
                        choosenNumber.get() == null ? "<i>Не выбрано</i>" : choosenNumber.get(),
                        text.isEmpty() ? "<i>не введен</i>" : text
                );
    }

    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
