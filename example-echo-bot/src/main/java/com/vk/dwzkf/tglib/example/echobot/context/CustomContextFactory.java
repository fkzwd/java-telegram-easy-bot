package com.vk.dwzkf.tglib.example.echobot.context;

import com.vk.dwzkf.tglib.botcore.context.MessageContextFactory;
import org.springframework.stereotype.Component;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Component
public class CustomContextFactory implements MessageContextFactory<CustomContext> {
    @Override
    public CustomContext create() {
        return new CustomContext();
    }
}
