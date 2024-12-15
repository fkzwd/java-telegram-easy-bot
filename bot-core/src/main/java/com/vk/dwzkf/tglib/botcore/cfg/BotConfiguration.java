package com.vk.dwzkf.tglib.botcore.cfg;

import com.vk.dwzkf.tglib.commons.cfg.BotConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration extends BotConfig {
    private boolean secure = true;
}
