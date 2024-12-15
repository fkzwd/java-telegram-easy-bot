package com.vk.dwzkf.tglib.botcore.context;

import lombok.*;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommandContext {
    private String command;
    private String[] commandArgs;
    private String rawCommand;
    private String rawArgs;
    private String botQualifier;
    private boolean hasArgs;

    public boolean hasArgs() {
        return hasArgs;
    }
}
