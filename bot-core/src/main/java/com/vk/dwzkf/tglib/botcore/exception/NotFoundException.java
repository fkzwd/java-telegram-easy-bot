package com.vk.dwzkf.tglib.botcore.exception;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class NotFoundException extends BotCoreRuntimeException {
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
