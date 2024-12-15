package com.vk.dwzkf.tglib.commons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Roman Shageev
 * @since 28.04.2023
 */
@RequiredArgsConstructor
@Getter
public enum  ChatStatus {
    MEMBER("member"),
    LEFT("left");

    private final String name;
}
