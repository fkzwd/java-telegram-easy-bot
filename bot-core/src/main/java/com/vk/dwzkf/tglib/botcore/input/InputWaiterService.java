package com.vk.dwzkf.tglib.botcore.input;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.enums.InputFlag;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.exception.OnInputException;
import com.vk.dwzkf.tglib.botcore.forms.actions.ActionType;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.FormRenderer;
import com.vk.dwzkf.tglib.botcore.handlers.DefaultTextMessageHandler;
import com.vk.dwzkf.tglib.botcore.service.FormSenderService;
import com.vk.dwzkf.tglib.botcore.service.UserFormService;
import com.vk.dwzkf.tglib.commons.utils.ArrayUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.vk.dwzkf.tglib.botcore.service.UserFormService.DEFAULT_ACCESS;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InputWaiterService extends DefaultTextMessageHandler {
    private final Map<String, LinkedList<InputWaiter>> chatWaiters = new ConcurrentHashMap<>();
    private final FormSenderService formSenderService;
    private final FormRenderer formRenderer;
    private final UserFormService userFormService;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    private static class InputWaiter {
        InputFlag[] inputFlags;
        InputAwait waiter;
        InputFilter matcher;
    }

    private LinkedList<InputWaiter> getChatWaiters(String chatId) {
        return chatWaiters.computeIfAbsent(chatId, key -> new LinkedList<>());
    }

    public synchronized void await(InputAwait waiter, MessageContext sourceMessage) {
        await(waiter, sourceMessage, inputContext -> true, InputFlag.values());
    }

    public synchronized void await(InputAwait waiter, MessageContext sourceMessage, InputFlag... flags) {
        await(waiter, sourceMessage, inputContext -> true, flags);
    }

    public synchronized void await(InputAwait waiter, MessageContext sourceMessage, InputFilter matcher, InputFlag... flags) {
        InputWaiter inputWaiter = new InputWaiter();
        inputWaiter.setWaiter(waiter);
        inputWaiter.setInputFlags(flags.length == 0 ? InputFlag.values() : flags);
        inputWaiter.setMatcher(matcher);
        chatWaiters.computeIfAbsent(sourceMessage.getChatId(), key -> new LinkedList<>())
                        .add(inputWaiter);
    }

    @Override
    public boolean match(MessageContext messageContext) {
        LinkedList<InputWaiter> queue = getChatWaiters(messageContext.getChatId());
        for (InputWaiter inputWaiter : queue) {
            if (inputWaiter.getMatcher().match(messageContext)) {
                if (ArrayUtil.intersect(inputWaiter.inputFlags, messageContext.inputFlags())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public synchronized void handle(MessageContext messageContext) throws BotCoreException {
        LinkedList<InputWaiter> queue = getChatWaiters(messageContext.getChatId());
        InputWaiter waiter = null;
        int idx = -1;
        for (int i = 0; i <= queue.size(); i++) {
            boolean match = queue.get(i).getMatcher().match(messageContext);
            if (match) {
                if (ArrayUtil.intersect(queue.get(i).inputFlags, messageContext.inputFlags())) {
                    waiter = queue.get(i);
                    idx = i;
                    break;
                }
            }
        }
        if (waiter == null) {
            throw new BotCoreException("No input await found");
        }
        List<AfterClickAction<?>> actions = null;
        try {
            actions = waiter.getWaiter().onInput(messageContext);
            //on success remove from queue
            queue.remove(idx);
        } catch (OnInputException e) {
            if (log.isDebugEnabled())
                log.info("Exception on input. {}", e.getMessage(), e);
            else
                log.info("Exception on input. {}", e.getMessage());
            actions = e.getActions();
            if (e.isInterrupt()) {
                queue.remove(idx);
            }
        }
        for (AfterClickAction<?> action : actions) {
            if (action.is(ActionType.ANSWER_TEXT)) {
                messageContext.doAnswer(action.as(ActionType.ANSWER_TEXT).getObject());
            } else if (action.is(ActionType.CREATE_FORM)) {
                AfterClickAction<Form> form = action.as(ActionType.CREATE_FORM);
                formSenderService.sendForm(
                        form.getObject(),
                        messageContext,
                        formRenderer,
                        userFormService,
                        DEFAULT_ACCESS
                );
            }
        }
    }
}
