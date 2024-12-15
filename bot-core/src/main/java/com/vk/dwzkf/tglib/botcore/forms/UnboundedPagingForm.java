package com.vk.dwzkf.tglib.botcore.forms;

/**
 * @author Roman Shageev
 * @since 16.11.2023
 */
public abstract class UnboundedPagingForm extends PagingForm {
    public void fillPagingButtons() {
        super.fillPagingButtons(getOffset()+getLimit()+1);
    }
}
