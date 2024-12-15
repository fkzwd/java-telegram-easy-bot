package com.vk.dwzkf.tglib.botcore.forms.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@AllArgsConstructor
//TODO: add action handler service
public class AfterClickAction<T> {
    @Getter
    private ActionType<T> actionType;
    //todo
    private Map<Object, Object> actionData = new LinkedHashMap<>();

    public AfterClickAction(ActionType<T> actionType) {
        this.actionType = actionType;
    }

    public void setObject(T object) {
        actionData.put(actionType, object);
    }


    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) actionData.get(actionType);
    }

    @SuppressWarnings("unchecked")
    public <R> R getObject(ActionType<R> actionType) {
        return (R) actionData.get(actionType);
    }

    public <R> boolean is(ActionType<R> checker) {
        if (checker == null) return false;
        return checker.equals(actionType);
    }

    @SuppressWarnings("unchecked")
    public <R> AfterClickAction<R> as(ActionType<R> caster) {
        if (!is(caster)) {
            throw new IllegalArgumentException("Unable to cast cause type mismatch");
        }
        return (AfterClickAction<R>) this;
    }
}
