package com.vk.dwzkf.tglib.botcore.bot.queue.cfg;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
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
public class SmartBotTaskQueueConfig extends RateLimitConfig {
    private DefaultBotTaskQueueConfig defaultBotTaskQueueConfig = new DefaultBotTaskQueueConfig(
            150L,
            TimeUnit.MILLISECONDS
    );
    private RateLimitConfig privateChatConfig;
    private RateLimitConfig groupChatConfig;
    private Map<String, RateLimitConfig> directConfig = new HashMap<>();
}
