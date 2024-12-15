package com.vk.dwzkf.tglib.botcore.context.photos;

import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;

/**
 * @author Roman Shageev
 * @since 30.11.2023
 */
public class DownloadFailedException extends BotCoreException {
    public DownloadFailedException() {
    }

    public DownloadFailedException(String message) {
        super(message);
    }

    public DownloadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
