package com.vk.dwzkf.tglib.botcore.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Getter
@Setter
public class TextMessageHandleException extends BotCoreException {
    private String message;

    public TextMessageHandleException() {
    }

    public TextMessageHandleException(String message) {
        super(message);
    }

    public TextMessageHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
