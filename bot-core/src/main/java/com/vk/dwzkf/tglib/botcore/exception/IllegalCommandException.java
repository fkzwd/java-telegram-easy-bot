package com.vk.dwzkf.tglib.botcore.exception;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class IllegalCommandException extends BotCoreRuntimeException {
    public IllegalCommandException() {
    }

    public IllegalCommandException(String message) {
        super(message);
    }

    public IllegalCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
