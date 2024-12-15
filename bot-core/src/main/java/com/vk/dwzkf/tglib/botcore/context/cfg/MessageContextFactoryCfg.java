package com.vk.dwzkf.tglib.botcore.context.cfg;

import com.vk.dwzkf.tglib.botcore.context.DefaultMessageContextFactory;
import com.vk.dwzkf.tglib.botcore.context.MessageContextFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roman Shageev
 * @since 11.12.2024
 */
@Configuration
public class MessageContextFactoryCfg {
    @ConditionalOnMissingBean(MessageContextFactory.class)
    @Bean
    public DefaultMessageContextFactory messageContextCreator() {
        return new DefaultMessageContextFactory();
    }
}
