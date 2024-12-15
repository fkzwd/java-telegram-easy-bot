package com.vk.dwzkf.tglib.botcore.forms.impl;

import com.vk.dwzkf.tglib.botcore.forms.Form;

/**
 * @author Roman Shageev
 * @since 20.11.2023
 */
public class ErrorForm extends Form {
    public ErrorForm(Form parent, String title, String message){
        setTitle(title);
        setParent(parent);
        setText(message);
        createControls(parent != null, true);
    }
}
