package com.vk.dwzkf.tglib.botcore.handlers.configurator;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
public class RouteCommandConfigurationException extends IllegalStateException {
    public RouteCommandConfigurationException(String command, Class<?> existing, Class<?> overriding) {
        super(
                "Handler for command '%s' already exists. Existing '%s' overriding '%s'"
                        .formatted(command, existing.getName(), overriding.getName())
        );
    }
}
