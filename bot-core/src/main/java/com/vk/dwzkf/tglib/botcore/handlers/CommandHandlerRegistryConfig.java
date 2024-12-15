package com.vk.dwzkf.tglib.botcore.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CommandHandlerRegistryConfig {
    public interface CommandConfigurator {
        void configure(CommandHandlerRegistryConfig registryConfig);
    }

    private final ApplicationContext applicationContext;
    private final CommandHandlerRegistry commandHandlerRegistry;
    private final List<Map<Command, Class<? extends CommandHandler<?>>>> configs = Collections.synchronizedList(new ArrayList<>());

    @Autowired(required = false)
    public void applyConfigurators(List<CommandConfigurator> configurators) {
        configurators.forEach(c -> c.configure(this));
    }

    public <T extends CommandHandler<?>> void setHandler(Command command, T commandHandler) {
        CommandHandler<?> existing = commandHandlerRegistry.getHandler(command.getCommand());
        if (existing != null && existing != commandHandler) {
            throw new IllegalStateException(
                    String.format(
                            "Handler for command '%s' already registered. Registered: '%s', override: '%s'",
                            command.getCommand(),
                            existing.getClass(),
                            commandHandler.getClass()
                    )
            );
        }
        if (existing != null) {
            log.warn("Handler for command '{}' already registered: {}",
                    command.getCommand(),
                    existing.getClass().getName()
            );
        }
        commandHandlerRegistry.setHandler(command, commandHandler);
    }

    //TODO: generic
    public void configure(Map<Command, Class<? extends CommandHandler<?>>> config) {
        configs.add(config);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void internalConfigure() {
        for (Map<Command, Class<? extends CommandHandler<?>>> config : configs) {
            config.forEach((key, value) -> {
                String beanName = value.getSimpleName();
                beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                Object bean = applicationContext.getBean(beanName);
                if (!(bean instanceof CommandHandler<?> handler)) {
                    throw new IllegalStateException(
                            String.format(
                                    "Not found bean for command %s of type %s",
                                    key,
                                    value
                            )
                    );
                }
                setHandler(key, handler);
            });
        }
    }
}
