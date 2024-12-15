package com.vk.dwzkf.tglib.botcore.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Command {
    private String command;
    private String description;
}
