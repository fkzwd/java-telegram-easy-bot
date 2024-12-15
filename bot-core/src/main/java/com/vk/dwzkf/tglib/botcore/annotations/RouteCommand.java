package com.vk.dwzkf.tglib.botcore.annotations;

import com.vk.dwzkf.tglib.botcore.handlers.CommandHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *  Используется на бинах имплементирующих {@link CommandHandler}
 *  либо с подходящим методом аннотированным {@link RouteCommandHandler}
 *
 *  Такие бины будут зарегистрированы как обработчики команд с заданными {@link #command()}
 *  @see RouteCommandHandler RouteCommandHandler
 *  @see CommandHandler CommandHandler
 * </pre>
 * @author Roman Shageev
 * @since 14.12.2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RouteCommand {
    String NONE = "\n\n\t\n\t\t\t\n \n\t\t\t\t\n\t\n \n\n\t\n\t\t\t\n \n\t\t\t\t\t\n\n \n\n\t\n\t\t\t\t \n\t\n\n\n\t\t\n";
    String command();
    String description() default NONE;
}
