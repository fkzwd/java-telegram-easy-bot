package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.annotations.RouteCommandHandler;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.enums.InputFlag;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.OnInputException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.actions.ActionType;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.input.InputAwait;
import com.vk.dwzkf.tglib.botcore.input.InputWaiterService;
import com.vk.dwzkf.tglib.example.echobot.context.CustomContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Shageev
 * @since 16.12.2024
 */
@RouteCommand(command = "chained")
@Service
@RequiredArgsConstructor
public class ChainedInputWait {
    private final InputWaiterService inputWaiterService;

    private static class ChainedInputContext {
        private String firstMame;
        private String lastName;
        private int age;
    }

    @RouteCommandHandler
    public void handle(CustomContext ctx) throws BotCoreException  {
        ChainedInputContext context = new ChainedInputContext();
        ctx.doReply("Введите имя").getMessageId();
        inputWaiterService
                .await(inputContext -> {
                            String input = inputContext.getMessage();
                            if (!input.matches("[a-zA-Z]+")) {
                                throw new OnInputException(
                                        List.of(
                                                ActionType.REPLY_TEXT.createAction("Некорректный ввод. Введите имя")
                                        )
                                ).setInterrupt(false);
                            }
                            context.firstMame = input;
                            return List.of(
                                    ActionType.REPLY_TEXT.createAction("Введите Фамилию")
                            );
                        }, ctx
                )
                .thenAwait(inputContext -> {
                    String message = inputContext.getMessage();
                    if (!message.matches("[a-zA-Z]+")) {
                        throw new OnInputException(
                                List.of(
                                        ActionType.REPLY_TEXT.createAction("Некорректный ввод. Введите фамилию")
                                )
                        ).setInterrupt(false);
                    }
                    context.lastName = message;
                    return List.of(
                            ActionType.REPLY_TEXT.createAction("Введите возраст")
                    );
                }).thenAwait(inputContext -> {
                    String message = inputContext.getMessage();
                    if (!message.matches("\\d+")) {
                        throw new OnInputException(
                                List.of(
                                        ActionType.REPLY_TEXT.createAction("Некорректный ввод. Введите возраст")
                                )
                        ).setInterrupt(false);
                    }
                    context.age = Integer.parseInt(message);
                    return List.of(
                            ActionType.CREATE_FORM.createAction(createDataForm(context))
                    );
                });
    }

    private Form createDataForm(ChainedInputContext ctx) {
        Form form = new Form();
        form.setTextProvider(() -> {
            return """
                    <b>Имя:</b> %s
                    <b>Фамилия:</b> %s
                    <b>Возраст:</b> %s
                    """.formatted(
                    ctx.firstMame,
                    ctx.lastName,
                    ctx.age
            );
        });
        form.createControls(false, true);
        return form;
    }
}
