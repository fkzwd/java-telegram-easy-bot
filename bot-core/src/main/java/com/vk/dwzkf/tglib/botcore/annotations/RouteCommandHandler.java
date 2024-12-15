package com.vk.dwzkf.tglib.botcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Аннотация позволяет определить метод как обработчик команды
 * Используется совместно с {@link RouteCommand}
 * Аннотированный метод на вход должен принимать <code>T extends MessageContext</code>
 * </pre>
 *
 * <pre>
 * На классе с аннотацией {@link RouteCommand} должен быть ровно один метод
 * с аннотацией {@link RouteCommandHandler}
 * </pre>
 *
 * <pre>
 * {@code
 *      public void handle(MessageContext ctx) throws ...
 * }
 * </pre>
 * @see com.vk.dwzkf.tglib.botcore.handlers.CommandHandler
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RouteCommandHandler {
}
