package com.vk.dwzkf.tglib.botcore.forms.buttons;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.forms.actions.ActionType;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 11.11.2023
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class FormButton {
    private String title;
    private OnButtonClick onButtonClick;
    private Form owner;

    public FormButton(String title, SimpleOnButtonClick onClick, Form owner) {
        this.title = title;
        this.owner = owner;
        this.onButtonClick = buildOnClick(onClick);
    }

    public OnButtonClick buildOnClick(SimpleOnButtonClick onClick) {
        return (btn, parent1, ctx) -> {
            Form form = onClick.onClick(btn);
            AfterClickAction<Form> result = new AfterClickAction<>(ActionType.UPDATE_FORM);
            result.setObject(form);
            return List.of(result);
        };
    }

    public void setOnClick(SimpleOnButtonClick onClick) {
        this.onButtonClick = buildOnClick(onClick);
    }

    public FormButton(String title, OnButtonClick onButtonClick, Form owner) {
        this.title = title;
        this.onButtonClick = onButtonClick;
        this.owner = owner;
    }

    public List<AfterClickAction<?>> onClick(Form owner, MessageContext initiator) {
        if (onButtonClick == null) {
            AfterClickAction<Form> formAfterClickAction = new AfterClickAction<>(ActionType.UPDATE_FORM);
            formAfterClickAction.setObject(owner);
            return List.of(formAfterClickAction);
        }
        return onButtonClick.onClick(this, owner, initiator);
    }

    public static FormButton createBackButton(Form owner) {
        return new FormButton(
                "Назад",
                btn -> owner.getParent(),
                owner
        );
    }

    public static FormButton createCloseButton(Form owner) {
        return new FormButton(
                "Закрыть",
                (btn, parent1, ctx) -> {
                    AfterClickAction<Void> afterClickAction = new AfterClickAction<>(ActionType.DELETE_MESSAGE);
                    return List.of(afterClickAction);
                },
                owner
        );
    }
}
