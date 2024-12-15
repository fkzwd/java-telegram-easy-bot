package com.vk.dwzkf.tglib.botcore.handlers;

import com.vk.dwzkf.tglib.botcore.input.InputWaiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TextMessageHandlerRegistryConfig {
    private final ApplicationContext applicationContext;
    private final TextMessageHandlerRegistry textMessageHandlerRegistry;
    private final List<Class<? extends TextMessageHandler>> cfg = Collections.synchronizedList(new ArrayList<>());

    public interface TextHandlerConfigurator {
        void configure(TextMessageHandlerRegistryConfig registryConfig);
    }

    @Autowired(required = false)
    public void applyConfigurators(List<TextHandlerConfigurator> configurators) {
        configurators.forEach(c -> c.configure(this));
    }


    public void addHandler(TextMessageHandler textMessageHandler) {
        textMessageHandlerRegistry.addHandler(textMessageHandler);
    }

    public void addHandlers(List<TextMessageHandler> handlers) {
        textMessageHandlerRegistry.addHandlers(handlers);
    }

    public void configure(Class<? extends TextMessageHandler> bean) {
        if (!cfg.contains(bean)) {
            cfg.add(bean);
        } else {
            log.warn("Text handler '{}' already exists", bean.getName());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        addHandler(findHandler(InputWaiterService.class));
        for (Class<? extends TextMessageHandler> handler : cfg) {
            addHandler(findHandler(handler));
        }
    }

    private TextMessageHandler findHandler(Class<? extends TextMessageHandler> bean) {
        Map<String, ? extends TextMessageHandler> beans = applicationContext.getBeansOfType(bean);
        return beans.entrySet().stream()
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    public void configure(List<Class<? extends TextMessageHandler>> config) {
        config.forEach(this::configure);
    }
}
