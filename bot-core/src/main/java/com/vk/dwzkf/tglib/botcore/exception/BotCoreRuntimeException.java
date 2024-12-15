package com.vk.dwzkf.tglib.botcore.exception;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class BotCoreRuntimeException extends RuntimeException {
    public BotCoreRuntimeException() {
    }

    public BotCoreRuntimeException(String message) {
        super(message);
    }

    public BotCoreRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
