package com.vk.dwzkf.tglib.example.echobot.forms;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.example.echobot.forms.simple_form.RefreshButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Roman Shageev
 * @since 16.12.2024
 */
public class CurrentTimeForm extends Form {
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CurrentTimeForm() {
        setTitle("<b>Заголовок формы</b>");
        setTextProvider(() ->
                "<b>Текущее время:</b> <code>%s</code>"
                        .formatted(LocalDateTime.now().format(DATE_TIME_FORMAT))
        );
        addRow().addButton(new RefreshButton(this));
        createControls(false,true); //создаем кнопку закрытия формы
    }
}
