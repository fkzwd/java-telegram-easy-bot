package com.vk.dwzkf.tglib.botcore.bot.queue.cfg;

import lombok.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Roman Shageev
 * @since 30.12.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RateLimitConfig {
    private int limit = 2;
    private TimeUnit unit = TimeUnit.SECONDS;
    private int window = 1;
}
