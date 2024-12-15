package com.vk.dwzkf.tglib.botcore.forms.buttons;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.forms.Form;

import java.util.List;

/**
 * @author Roman Shageev
 * @since 01.12.2023
 */
public interface OnButtonClick {
    List<AfterClickAction<?>> onClick(FormButton thisButton, Form buttonOwner, MessageContext initiator);
}
