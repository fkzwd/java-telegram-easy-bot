package com.vk.dwzkf.tglib.botcore.exception;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class BotCoreException extends Exception {
    public BotCoreException() {
    }

    public BotCoreException(String message) {
        super(message);
    }

    public BotCoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
