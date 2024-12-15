package com.vk.dwzkf.tglib.botcore.forms.buttons;

import com.vk.dwzkf.tglib.botcore.forms.Form;

/**
 * @author Roman Shageev
 * @since 11.11.2023
 */
public interface SimpleOnButtonClick {
    Form onClick(FormButton button);
}
