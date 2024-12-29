package com.vk.dwzkf.tglib.botcore.bot.queue.cfg;

import com.vk.dwzkf.tglib.botcore.bot.queue.enums.BotQueueType;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roman Shageev
 * @since 30.12.2024
 */
@Configuration
@ConfigurationProperties("bot.task-queue.queue-factory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BotQueueFactoryConfig {
    private BotQueueType type = BotQueueType.SMART;
}
