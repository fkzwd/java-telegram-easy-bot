package com.vk.dwzkf.tglib.botcore.context;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class ArgumentParser {
    public String[] parse(String message) {
        return message.split(" ");
    }
}
