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
@ConfigurationProperties("bot.task-queue.default-bot-task-queue")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DefaultBotTaskQueueConfig {
    private Long taskExecutionRate = 1L;
    private TimeUnit taskExecutionTimeUnit = TimeUnit.SECONDS;
}
