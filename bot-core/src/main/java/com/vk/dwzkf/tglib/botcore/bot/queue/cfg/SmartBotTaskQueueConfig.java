package com.vk.dwzkf.tglib.botcore.bot.queue.cfg;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Roman Shageev
 * @since 25.12.2024
 */
@Configuration
@ConfigurationProperties("bot.task-queue.smart-bot-task-queue")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SmartBotTaskQueueConfig {
    private int limit = 2;
    private TimeUnit unit = TimeUnit.SECONDS;
    private int window = 1;
    private DefaultBotTaskQueueConfig defaultBotTaskQueueConfig = new DefaultBotTaskQueueConfig(
            150L,
            TimeUnit.MILLISECONDS
    );
}
