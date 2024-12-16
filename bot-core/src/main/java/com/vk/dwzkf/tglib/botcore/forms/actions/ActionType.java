package com.vk.dwzkf.tglib.botcore.forms.actions;

import com.vk.dwzkf.tglib.botcore.forms.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
public class ActionType<T> {
    public static final ActionType<Form> UPDATE_FORM = new ActionType<>(Form.class);
    /**
     * Предназначен только для удаления формы для которой была нажата кнопка
     */
    public static final ActionType<Void> DELETE_MESSAGE = new ActionType<>(Void.class);
    public static final ActionType<Form> CREATE_FORM = new ActionType<>(Form.class);
    public static final ActionType<String> ANSWER_TEXT = new ActionType<>(String.class);

    private final Class<T> dataClass;
    private ActionType(Class<T> dataClass){
        this.dataClass = dataClass;
    }

    public AfterClickAction<T> createAction(T data) {
        AfterClickAction<T> action = new AfterClickAction<>(this);
        action.setObject(data);
        return action;
    }
}
