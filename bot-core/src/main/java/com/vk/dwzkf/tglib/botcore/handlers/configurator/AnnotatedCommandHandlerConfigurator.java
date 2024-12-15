package com.vk.dwzkf.tglib.botcore.handlers.configurator;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.annotations.RouteCommandHandler;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.handlers.Command;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandlerRegistryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AnnotatedCommandHandlerConfigurator implements CommandHandlerRegistryConfig.CommandConfigurator {
    private final ApplicationContext applicationContext;

    @Override
    public void configure(CommandHandlerRegistryConfig registryConfig) {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RouteCommand.class);
        beansWithAnnotation.forEach((beanName, bean) -> {
            RouteCommand annotationValue = bean.getClass().getAnnotation(RouteCommand.class);
            CommandHandler<?> commandHandler = resolveCommandHandlerForBean(bean, annotationValue);
            Command command = resolveCommandForBean(commandHandler, bean, annotationValue);
            if (commandHandler instanceof ProxyAnnotationHandler<?> proxyAnnotationHandler) {
                log.info(
                        "Registered CommandHandler proxy for class {} with method {}",
                        bean.getClass().getName(),
                        proxyAnnotationHandler.getHandlerMethod().toString()
                );
            }
            registryConfig.setHandler(command, commandHandler);
        });
    }

    private Command resolveCommandForBean(CommandHandler<?> resolvedHandler, Object bean, RouteCommand annotationValue) {
        String commandName = annotationValue.command();
        String commandDescription = annotationValue.description()
                .equals(RouteCommand.NONE) ? commandName : annotationValue.description();
        return new Command(commandName, commandDescription);
//        throw new IllegalStateException(
//                "Unable to determine command for annotated CommandHandler bean %s"
//                        .formatted(bean.getClass().getName())
//        );
    }

    private CommandHandler<?> resolveCommandHandlerForBean(Object bean, RouteCommand annotationValue) {
        if (bean instanceof CommandHandler<?>) {
            return (CommandHandler<?>) bean;
        }
        Method handlerMethod = determineHandlerMethod(bean);
        return new ProxyAnnotationHandler<>(handlerMethod, bean);
    }

    private Method determineHandlerMethod(Object bean) {
        Method[] methods = bean.getClass().getMethods();
        List<Method> annotatedMethods = Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(RouteCommandHandler.class))
                .collect(Collectors.toList());
        List<Method> candidates = annotatedMethods.stream()
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> MessageContext.class.isAssignableFrom(m.getParameterTypes()[0]))
                .toList();
        annotatedMethods.removeIf(candidates::contains);
        if (!annotatedMethods.isEmpty()) {
            //аннотированы неподходящие методы, накажем юзера за беспредел
            throwWrongMethodsAnnotated(bean, annotatedMethods);
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        if (candidates.size() > 1) {
            throwAmbigiousCandidatesFound(bean, candidates);
        }
        throw new IllegalStateException(
                """
                        Unable to determine CommandHandlerMethod for annotated bean %s, required 1 method but found 0
                        Implement %s or use %s annotation
                        """
                        .formatted(
                                bean.getClass().getName(),
                                CommandHandler.class.getName(),
                                RouteCommandHandler.class.getName()
                        )
        );
    }

    private static void throwAmbigiousCandidatesFound(Object bean, List<Method> candidates) {
        throw new IllegalStateException(
                """
                        Unable to determine CommandHandlerMethod for annotated bean %s
                        Found %s methods but required 1
                        Found methods: %s
                        """
                        .formatted(
                                bean.getClass().getName(),
                                candidates.size(),
                                candidates.stream()
                                        .map(Method::toString)
                                        .toList()
                        )
        );
    }

    private void throwWrongMethodsAnnotated(Object bean, List<Method> annotatedMethods) {
        throw new IllegalStateException(
                """
                        Annotated methods signature is wrong; Should have 1 parameter with type(subtype) of %s
                        Class: %s
                        Methods: %s
                        """
                        .formatted(
                                MessageContext.class.getName(),
                                bean.getClass().getName(),
                                annotatedMethods.stream()
                                        .map(Method::toString)
                                        .toList()
                        )
        );
    }
}
