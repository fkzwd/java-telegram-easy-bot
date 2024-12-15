package com.vk.dwzkf.tglib.botcore.handlers;

/**
 * @author Roman Shageev
 * @since 14.11.2023
 */
public class PreparedCommands {
    /**
     * prepared command for form dispatches
     * can user it or bound custom, no matter
     */
    public static final Command formDispatchCommand = new Command(
            "form",
            "form dispatcher command"
    );
}
