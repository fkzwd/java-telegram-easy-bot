package com.vk.dwzkf.tglib.example.echobot.forms.simple_form;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.enums.InputFlag;
import com.vk.dwzkf.tglib.botcore.exception.OnInputException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.actions.ActionType;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.botcore.input.InputAwait;
import com.vk.dwzkf.tglib.botcore.input.InputWaiterService;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 13.12.2024
 */
public class TextInputButton extends FormButton {
    private static final AfterClickAction<String> error = new AfterClickAction<>(ActionType.ANSWER_TEXT);
    static {
        error.setObject("Некорректный ввод.");
    }

    public TextInputButton(StringBuilder inputListener, InputWaiterService waiterService, Form parent) {
        setOwner(parent);
        setTitle("Ввести текст");
        setOnButtonClick((btn, owner, initiator) -> {
            waiterService.await(createInputAwait(inputListener, parent), initiator);
            AfterClickAction<String> action = new AfterClickAction<>(ActionType.ANSWER_TEXT);
            action.setObject("Введите текст");
            AfterClickAction<Void> deleteForm = new AfterClickAction<>(ActionType.DELETE_MESSAGE);
            return List.of(action, deleteForm);
        });
    }

    private static InputAwait createInputAwait(StringBuilder inputListener, Form parent) {
        return input -> {
            if (!input.getInputFlags().contains(InputFlag.HAS_TEXT)) {
                throw new OnInputException(List.of(error)).setInterrupt(true);
            }
            if (input.getMessage().isBlank() || !input.getMessage().matches("^[a-zA-Z0-9А-Яа-я]+$")) {
                throw new OnInputException(List.of(error)).setInterrupt(true);
            }
            inputListener.delete(0, inputListener.length());
            inputListener.append(input.getMessage());
            ActionType<Form> createForm = ActionType.CREATE_FORM;
            AfterClickAction<Form> createFormAction = new AfterClickAction<>(createForm);
            createFormAction.setObject(parent);
            return List.of(createFormAction);
        };
    }
}
