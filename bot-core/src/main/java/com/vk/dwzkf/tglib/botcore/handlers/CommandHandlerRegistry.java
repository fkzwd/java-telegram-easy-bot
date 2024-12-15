package com.vk.dwzkf.tglib.botcore.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@Getter
@Setter
@Slf4j
public class CommandHandlerRegistry {
    @Getter(AccessLevel.NONE)
    private final Map<String, CommandHandler> config = new ConcurrentHashMap<>();
    private CommandHandler notFoundCommandHandler = new DefaultNotFoundCommandHandler();


    void setHandler(Command command, CommandHandler commandHandler) {
        config.put(command.getCommand(), commandHandler);
        log.info(
                "Registered command mapping for command '/{}' with handler '{}'",
                command.getCommand(),
                commandHandler.getName()
        );
    }

    /**
     *
     * @param command команда
     * @return nullable
     */
    public CommandHandler getHandler(String command) {
        return config.get(command);
    }

    public CommandHandler getOrDefault(String command) {
        return config.getOrDefault(command, notFoundCommandHandler);
    }

    public CommandHandler getDefaultHandler() {
        return notFoundCommandHandler;
    }
}
