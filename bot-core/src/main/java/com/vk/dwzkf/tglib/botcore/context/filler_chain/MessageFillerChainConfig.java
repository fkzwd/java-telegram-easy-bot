package com.vk.dwzkf.tglib.botcore.context.filler_chain;

import com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
@RequiredArgsConstructor
public class MessageFillerChainConfig {
    private final ApplicationContext applicationContext;

    private final List<Class<? extends ContextFiller<?>>> fillerClasses = new LinkedList<>();
    {
        fillerClasses.addAll(
                List.of(
                        BotNameFiller.class,
                        ChatDataFiller.class,
                        CaptionsFiller.class,
                        RawDataFiller.class,
                        CommandDataFiller.class,
                        SimpleMessageFiller.class,
                        UserInfoFiller.class,
                        PhotosFiller.class,
                        InputTypeFiller.class,
                        DefaultAuthorizationFiller.class
                )
        );
    }

    public <T extends ContextFiller<?>> void addFiller(Class<T> fillerClass) {
        fillerClasses.add(fillerClass);
    }

    public void addFillers(List<Class<? extends ContextFiller<?>>> fillers) {
        fillerClasses.addAll(fillers);
    }

    public MessageFillerChain createChain() {
        List<ContextFiller<?>> fillers = fillerClasses.stream()
                .map(applicationContext::getBean)
                .collect(Collectors.toList());
        MessageFillerChain messageFillerChain = new MessageFillerChain();
        messageFillerChain.setFillers(fillers);
        return messageFillerChain;
    }
}
