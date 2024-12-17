package com.vk.dwzkf.tglib.botcore.exception;

import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
public class OnInputException extends BotCoreException {
    @Getter
    private List<AfterClickAction<?>> actions;
    @Accessors(chain = true)
    @Setter
    @Getter
    private boolean interrupt = false;

    public OnInputException(List<AfterClickAction<?>> actions) {
        super("Bad input received");
        this.actions = actions;
    }

    public OnInputException(List<AfterClickAction<?>> actions, String message) {
        super(message);
        this.actions = actions;
    }

    public OnInputException(List<AfterClickAction<?>> actions, String message, Throwable cause) {
        super(message, cause);
        this.actions = actions;
    }
}
