package com.vk.dwzkf.tglib.botcore.annotations;

import com.vk.dwzkf.tglib.botcore.handlers.TextMessageHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     Используется на бинах имплементирующих интерфейс {@link TextMessageHandler}
 *     Такие бины будут зарегистрированы как обработчики текстовых сообщений
 * </pre>
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TextHandler {
}
