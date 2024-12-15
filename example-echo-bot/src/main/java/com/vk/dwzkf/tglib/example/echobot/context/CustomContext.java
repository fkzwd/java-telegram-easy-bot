package com.vk.dwzkf.tglib.example.echobot.context;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Getter
@Setter
public class CustomContext  extends MessageContext {
    private String uuid = UUID.randomUUID().toString();
}
