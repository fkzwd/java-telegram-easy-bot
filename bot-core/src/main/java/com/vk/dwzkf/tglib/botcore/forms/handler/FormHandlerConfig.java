package com.vk.dwzkf.tglib.botcore.forms.handler;

import com.vk.dwzkf.tglib.botcore.handlers.Command;
import com.vk.dwzkf.tglib.botcore.handlers.PreparedCommands;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
@Configuration
@ConfigurationProperties("bot.form-handler.dispatch-command")
@Setter
public class FormHandlerConfig {
    private String command = PreparedCommands.formDispatchCommand.getCommand();
    private String description = PreparedCommands.formDispatchCommand.getDescription();

    public Command getDispatchCommand() {
        return new Command(command, description);
    }
}
