package com.vk.dwzkf.tglib.example.echobot.forms.simple_form;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.botcore.forms.buttons.SimpleOnButtonClick;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
public class IncrementButton extends FormButton implements SimpleOnButtonClick {
    private AtomicInteger counter = new AtomicInteger(0);
    private int max = 1;

    public IncrementButton(String title, Form owner, int max, AtomicInteger counter) {
        setTitle(title);
        setOwner(owner);
        setOnClick(this);
        this.counter = counter;
        this.max = max;
    }

    @Override
    public Form onClick(FormButton button) {
        counter.getAndAccumulate(max, (a,b) -> ++a % b);
        return button.getOwner();
    }
}
