package com.vk.dwzkf.tglib.example.echobot.commands;

import com.vk.dwzkf.tglib.botcore.annotations.RouteCommand;
import com.vk.dwzkf.tglib.botcore.annotations.RouteCommandHandler;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.OnInputException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.actions.ActionType;
import com.vk.dwzkf.tglib.botcore.input.InputAwait;
import com.vk.dwzkf.tglib.botcore.input.InputWaiterService;
import com.vk.dwzkf.tglib.example.echobot.context.CustomContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Roman Shageev
 * @since 16.12.2024
 */
@RouteCommand(command = "chained")
@Service
@RequiredArgsConstructor
public class ChainedInputWait {
    private final InputWaiterService inputWaiterService;

    @RouteCommandHandler
    public void handle(CustomContext ctx) throws BotCoreException {
        ChainedInputContext context = new ChainedInputContext();
        ctx.doReply("Введите имя");
        inputWaiterService
                .await(
                        getSimpleString(
                                value -> context.firstName = value,
                                "Некорректный ввод. Введите имя",
                                "Введите фамилию"
                        ),
                        ctx
                )
                .thenAwait(getSimpleString(
                        value -> context.lastName = value,
                        "Некорректный ввод. Введите фамилию",
                        "Введите возраст"
                ))
                .thenAwait(getAge(context));
    }

    private InputAwait getAge(ChainedInputContext context) throws OnInputException {
        return inputContext -> {
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
        };
    }

    private static InputAwait getSimpleString(
            Consumer<String> valueConsumer,
            String errorMessage,
            String replyMessage
    ) throws OnInputException {
        return inputContext -> {
            String message = inputContext.getMessage();
            if (!message.matches("[a-zA-Z]+")) {
                throw new OnInputException(
                        List.of(
                                ActionType.REPLY_TEXT.createAction(errorMessage)
                        )
                ).setInterrupt(false);
            }
            valueConsumer.accept(message);
            return List.of(
                    ActionType.REPLY_TEXT.createAction(replyMessage)
            );
        };
    }

    private Form createDataForm(ChainedInputContext ctx) {
        Form form = new Form();
        form.setTextProvider(() -> {
            return """
                    <b>Имя:</b> %s
                    <b>Фамилия:</b> %s
                    <b>Возраст:</b> %s
                    """.formatted(
                    ctx.firstName,
                    ctx.lastName,
                    ctx.age
            );
        });
        form.createControls(false, true);
        return form;
    }

    private static class ChainedInputContext {
        private String firstName;
        private String lastName;
        private int age;
    }
}
