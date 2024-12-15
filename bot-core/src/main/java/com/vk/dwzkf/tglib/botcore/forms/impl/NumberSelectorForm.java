package com.vk.dwzkf.tglib.botcore.forms.impl;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.listeners.NumberInputListener;
import lombok.Setter;

/**
 * @author Roman Shageev
 * @since 14.11.2023
 */
@Setter
public class NumberSelectorForm extends SelectorForm<Integer> {
    public NumberSelectorForm(
            Form parent,
            NumberInputListener inputListener,
            int magicConstant,
            int step
    ) {
       this(parent, inputListener, magicConstant, step, -1);
    }
    /**
     * @param parent        parent form
     * @param inputListener listener on input chosen
     * @param magicConstant % 5 == 0; max = 20 <br/>see:<br/>
     *                        {@link #BUTTONS_PER_PAGE_TINY},<br/>
     *                        {@link #BUTTONS_PER_PAGE_SMALL},<br/>
     *                        {@link #BUTTONS_PER_PAGE_MEDIUM},<br/>
     *                        {@link #BUTTONS_PER_PAGE_HIGH},<br/>
     * @param step          min 1, if out of bounds forced to 1 if less
     * @param stickyFirstValue if > -1 then would be first
     */
    public NumberSelectorForm(
            Form parent,
            NumberInputListener inputListener,
            int magicConstant,
            int step,
            int stickyFirstValue
    ) {
        super(parent, magicConstant);
        int actualStep = step < 0 ? 1 : step;
        setListener(inputListener);
        setValueProvider((idx) -> {
            if (stickyFirstValue < 0) {
                return (idx+1)*step;
            }
            if (idx == 0) {
                return actualStep == 1 ? idx + 1 : stickyFirstValue;
            }
            return actualStep == 1 ? idx + 1 : idx*actualStep;
        });
        setSizeProvider(() -> getOffset()+getLimit()+1);
    }
}
