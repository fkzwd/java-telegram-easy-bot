package com.vk.dwzkf.tglib.commons.enums;

import com.vk.dwzkf.tglib.commons.utils.EnumHelper;
import lombok.RequiredArgsConstructor;

/**
 * Type of the chat, one of “private”, “group” or “channel” or "supergroup"
 * @author Roman Shageev
 * @since 11.10.2023
 */
@RequiredArgsConstructor
public enum ChatType implements EnumHelper.Name {
    PRIVATE("private"),
    GROUP("group"),
    CHANNEL("channel"),
    SUPERGROUP("supergroup");

    private final String type;


    @Override
    public String getName() {
        return type;
    }
}
