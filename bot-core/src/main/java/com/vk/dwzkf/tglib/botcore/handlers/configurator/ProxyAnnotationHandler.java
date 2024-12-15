package com.vk.dwzkf.tglib.botcore.handlers.configurator;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Roman Shageev
 * @since 14.12.2024
 */
@RequiredArgsConstructor
@Slf4j
public class ProxyAnnotationHandler<T extends MessageContext> implements CommandHandler<T> {
    @Getter(AccessLevel.PACKAGE)
    private final Method handlerMethod;
    @Getter(AccessLevel.PACKAGE)
    private final Object methodOwner;
    private final Class<T> supportedType;

    ProxyAnnotationHandler(Method handlerMethod, Object methodOwner) {
        this.handlerMethod = handlerMethod;
        this.methodOwner = methodOwner;
        Class<?> parameterType = handlerMethod.getParameterTypes()[0];
        this.supportedType = (Class<T>) parameterType;
    }

    @Override
    public void handle(T messageContext) throws BotCoreException {
        try {
            handlerMethod.invoke(methodOwner, messageContext);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error on handle command at {} {}", handlerMethod, methodOwner.getClass().getName());
            if (e.getCause() instanceof BotCoreException) {
                log.debug("Cause is {} rethrow", BotCoreException.class.getName());
                throw (BotCoreException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> supports() {
        return supportedType;
    }

    @Override
    public String getName() {
        return methodOwner.getClass().getName();
    }
}
