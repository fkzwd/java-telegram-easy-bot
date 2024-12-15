package com.vk.dwzkf.tglib.botcore.forms.cfg;

import com.vk.dwzkf.tglib.botcore.forms.FormRenderer;
import com.vk.dwzkf.tglib.botcore.forms.handler.FormHandlerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roman Shageev
 * @since 11.12.2024
 */
@Configuration
public class FormRendererConfig {
    @Bean
    @ConditionalOnMissingBean(FormRenderer.class)
    public FormRenderer formRenderer(FormHandlerConfig formHandlerConfig) {
        return new FormRenderer(formHandlerConfig.getDispatchCommand().getCommand());
    }
}
