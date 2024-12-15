package com.vk.dwzkf.tglib.botcore.forms.actions;

import com.vk.dwzkf.tglib.botcore.forms.Form;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
public class ActionType<T> {
    public static final ActionType<Form> UPDATE_FORM = new ActionType<>(Form.class);
    public static final ActionType<Void> DELETE_MESSAGE = new ActionType<>(Void.class);
    public static final ActionType<Form> CREATE_FORM = new ActionType<>(Form.class);
    public static final ActionType<String> ANSWER_TEXT = new ActionType<>(String.class);

    private final Class<T> dataClass;
    private ActionType(Class<T> dataClass){
        this.dataClass = dataClass;
    }
}
