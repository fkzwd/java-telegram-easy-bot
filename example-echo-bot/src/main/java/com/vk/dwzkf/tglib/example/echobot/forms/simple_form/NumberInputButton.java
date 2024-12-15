package com.vk.dwzkf.tglib.example.echobot.forms.simple_form;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.botcore.forms.buttons.OnButtonClick;
import com.vk.dwzkf.tglib.botcore.forms.impl.NumberSelectorForm;
import com.vk.dwzkf.tglib.botcore.forms.impl.SelectorForm;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Roman Shageev
 * @since 13.12.2024
 */
public class NumberInputButton extends FormButton {
    public NumberInputButton(Form parent, AtomicReference<Integer> holder) {
        setTitle("Выбрать число");
        setOwner(parent);
        setOnClick(btn -> {
            return new NumberSelectorForm(
                    parent,
                    (ownerForm, value) -> {
                        holder.set(value);
                        return parent;
                    },
                    SelectorForm.BUTTONS_PER_PAGE_MEDIUM,
                    1
            );
        });
    }
}
