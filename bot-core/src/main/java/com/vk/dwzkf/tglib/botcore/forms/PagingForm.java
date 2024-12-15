package com.vk.dwzkf.tglib.botcore.forms;

import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Shageev
 * @since 12.11.2023
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
//TODO: refactor
public abstract class PagingForm extends Form {
    public static final String PAGING_GROUP = "paging";

    private int offset = 0;
    private int limit = 5;

    public abstract void onOffsetChanged();


    public void fillPagingButtons(int totalSize) {
        createGroup(PAGING_GROUP);
        clearGroup(PAGING_GROUP);
        List<FormButton> controls = new ArrayList<>();
        if (getOffset() > 0) {
            FormButton prevButton = new FormButton(
                    "«",
                    (btn) -> {
                        setOffset(getOffset()-getLimit());
                        onOffsetChanged();
                        return PagingForm.this;
                    },
                    PagingForm.this
            );
            controls.add(prevButton);
        }
        if (getLimit() + getOffset() < totalSize) {
            FormButton nextButton = new FormButton(
                    "»",
                    (btn) -> {
                        setOffset(getOffset()+getLimit());
                        onOffsetChanged();
                        return PagingForm.this;
                    },
                    PagingForm.this
            );
            controls.add(nextButton);
        }
        addRow(PAGING_GROUP, r -> {
            for (FormButton control : controls) {
                r.addButton(control);
            }
        });
    }
}
