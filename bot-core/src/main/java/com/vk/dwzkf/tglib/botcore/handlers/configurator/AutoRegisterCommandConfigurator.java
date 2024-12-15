package com.vk.dwzkf.tglib.botcore.handlers.configurator;

import com.vk.dwzkf.tglib.botcore.handlers.AutoRegisterCommand;
import com.vk.dwzkf.tglib.botcore.handlers.Command;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandlerRegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Component
@Slf4j
public class AutoRegisterCommandConfigurator implements CommandHandlerRegistryConfig.CommandConfigurator {
    private final Map<Command, Class<? extends CommandHandler<?>>> commands = new HashMap<>();

    @Autowired(required = false)
    public void applyAutoRegisterCommands(List<AutoRegisterCommand<?>> autoRegisterCommands) {
        for (AutoRegisterCommand<?> autoRegisterCommand : autoRegisterCommands) {
            commands.putIfAbsent(
                    autoRegisterCommand.routingCommand(),
                    autoRegisterCommand.getHandler()
            );
        }
    }

    @Override
    public void configure(CommandHandlerRegistryConfig registryConfig) {
        registryConfig.configure(commands);
    }
}
