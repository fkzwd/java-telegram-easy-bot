package com.vk.dwzkf.tglib.botcore.forms.handler;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.exception.BotCoreException;
import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.FormRenderer;
import com.vk.dwzkf.tglib.botcore.service.FormSenderService;
import com.vk.dwzkf.tglib.botcore.service.UserFormService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vk.dwzkf.tglib.botcore.service.UserFormService.DEFAULT_ACCESS;

/**
 * @author Roman Shageev
 * @since 12.11.2023
 */
public abstract class FormSender<T extends MessageContext> {
    @Autowired
    private FormSenderService formSenderService;
    @Autowired
    private FormRenderer formRenderer;
    @Autowired
    private UserFormService userFormService;

    public void sendForm(
            Form form, T messageContext
    ) throws BotCoreException {
        sendForm(
                form, messageContext, formRenderer, userFormService,
                DEFAULT_ACCESS
        );
    }

    public void sendForm(
            Form form, T messageContext,
            FormRenderer formRenderer,
            UserFormService userFormService
    ) throws BotCoreException {
        sendForm(
                form, messageContext, formRenderer, userFormService,
                DEFAULT_ACCESS
        );
    }

    public void sendForm(
            Form form, T messageContext,
            UserFormService.FormAccess formAccess
    ) throws BotCoreException {
        formSenderService.sendForm(form, messageContext, formRenderer, userFormService, formAccess);
    }

    public void sendForm(
            Form form, T messageContext,
            FormRenderer formRenderer,
            UserFormService userFormService,
            UserFormService.FormAccess formAccess
    ) throws BotCoreException {
        formSenderService.sendForm(form, messageContext, formRenderer, userFormService, formAccess);
    }
}
