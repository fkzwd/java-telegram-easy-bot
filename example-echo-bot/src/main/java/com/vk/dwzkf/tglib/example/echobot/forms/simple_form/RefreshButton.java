package com.vk.dwzkf.tglib.example.echobot.forms.simple_form;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
public class RefreshButton extends FormButton {
    public RefreshButton(Form owner) {
        super("Обновить", FormButton::getOwner, owner);
    }
}
