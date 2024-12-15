package com.vk.dwzkf.tglib.botcore.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Getter
@Setter
public class CommandHandleException extends BotCoreException {
    private String command;

    public CommandHandleException() {
    }

    public CommandHandleException(String message) {
        super(message);
    }

    public CommandHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
