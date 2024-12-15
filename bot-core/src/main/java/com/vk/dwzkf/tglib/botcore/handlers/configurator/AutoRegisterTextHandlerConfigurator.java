package com.vk.dwzkf.tglib.botcore.handlers.configurator;

import com.vk.dwzkf.tglib.botcore.handlers.AutoRegisterTextHandler;
import com.vk.dwzkf.tglib.botcore.handlers.TextMessageHandler;
import com.vk.dwzkf.tglib.botcore.handlers.TextMessageHandlerRegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Component
@Slf4j
public class AutoRegisterTextHandlerConfigurator implements TextMessageHandlerRegistryConfig.TextHandlerConfigurator {
    private final List<Class<? extends TextMessageHandler>> cfg = Collections.synchronizedList(new ArrayList<>());

    @Autowired(required = false)
    public void applyAutoRegisterHandlers(List<AutoRegisterTextHandler<?>> handlers) {
        for (AutoRegisterTextHandler<?> handler : handlers) {
            if (!cfg.contains(handler.getHandler())) {
                cfg.add(handler.getHandler());
            } else {
                log.warn("Text Handler already registered. Skipped {}", handler.getHandler());
            }
        }
    }

    @Override
    public void configure(TextMessageHandlerRegistryConfig registryConfig) {
        registryConfig.configure(cfg);
    }
}
