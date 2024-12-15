package com.vk.dwzkf.tglib.botcore.forms;

import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Form renderer, required dispatch command
 * @author Roman Shageev
 * @since 11.11.2023
 */
public class FormRenderer {
    private final String dispatchCommand;

    /**
     *
     * @param dispatchCommand used to render callback queries<br/>
     *                        that dispatches to FormCommandHandler bound command
     */
    public FormRenderer(String dispatchCommand) {
        this.dispatchCommand = dispatchCommand;
    }

    public InlineKeyboardMarkup render(Form form) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < form.getButtonGrid().size(); i++) {
            List<FormButton> row = form.getButtonGrid().get(i);
            List<InlineKeyboardButton> renderedRow = new ArrayList<>();
            for (int j = 0; j < row.size(); j++) {
                FormButton button = row.get(j);
                InlineKeyboardButton renderedButton = new InlineKeyboardButton(button.getTitle());
                renderedButton.setCallbackData(String.format("/"+ dispatchCommand +" %s %s", i, j));
                renderedRow.add(renderedButton);
            }
            buttons.add(renderedRow);
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    public String renderMessage(Form form) {
        String title = Objects.requireNonNullElse(form.getTitle(), "");
        String info = Objects.requireNonNullElse(form.renderText(), "");
        if (info.isBlank()) return title;
        if (title.isBlank()) return info;
        return title+"\n\n"+info;
    }
}
