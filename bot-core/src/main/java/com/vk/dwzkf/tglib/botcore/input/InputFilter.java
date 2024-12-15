package com.vk.dwzkf.tglib.botcore.input;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
public interface InputFilter {
    boolean match(MessageContext inputContext);

    default InputFilter and(InputFilter other) {
        return (ctx) -> {
            return match(ctx) && other.match(ctx);
        };
    }
}
