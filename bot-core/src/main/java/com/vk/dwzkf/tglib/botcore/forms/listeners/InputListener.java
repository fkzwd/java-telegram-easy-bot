package com.vk.dwzkf.tglib.botcore.forms.listeners;

import com.vk.dwzkf.tglib.botcore.forms.Form;

/**
 * @author Roman Shageev
 * @since 16.11.2023
 */
public interface InputListener<T> {
    Form onInput(Form ownerForm, T value);
}
