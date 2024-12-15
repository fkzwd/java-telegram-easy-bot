package com.vk.dwzkf.tglib.botcore.exception;

import com.vk.dwzkf.tglib.botcore.context.MessageContextFactory;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
public class SupportedClassTypeMismatchException extends IllegalStateException {
    public SupportedClassTypeMismatchException(String message, Class<?> handler, Class<?> supported, Class<?> received) {
        super(String.format(
                """
                   %s
                   Handler %s supports %s but received %s
                   Create corresponding bean for creation context %s by implementing %s
                   """
                ,
                message,
                handler.getName(),
                supported.getName(),
                received.getName(),
                supported.getName(),
                MessageContextFactory.class.getName()
        ));
    }
}
