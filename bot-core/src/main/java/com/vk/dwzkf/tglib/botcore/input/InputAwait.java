package com.vk.dwzkf.tglib.botcore.input;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.OnInputException;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
public interface InputAwait {
    List<AfterClickAction<?>> onInput(MessageContext inputContext) throws OnInputException;
}
