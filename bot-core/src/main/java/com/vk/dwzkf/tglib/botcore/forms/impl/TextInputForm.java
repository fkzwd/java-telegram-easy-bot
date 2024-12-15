package com.vk.dwzkf.tglib.botcore.forms.impl;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.botcore.forms.listeners.TextInputListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 16.11.2023
 */
public class TextInputForm extends SelectorForm<String> {
    public static final String TEXT_INPUT_CONTROLS = "text-input-controls";
    private static final List<String> values =
            "йцукенгшщзхъфывапролджэячсмитьбю1234567890".chars()
                    .mapToObj(i -> String.valueOf((char) i))
                    .collect(Collectors.toList());
    private boolean lowerCase = true;
    private StringBuilder sb = new StringBuilder();

    public void setDefaultValue(String value) {
        clearValue();
        if (value != null) {
            sb.append(value);
        }
    }

    public void clearValue() {
        sb.delete(0, sb.length());
    }

    public TextInputForm(Form parent, TextInputListener inputListener) {
        super(parent);
        setListener((parent1, value) -> {
            sb.append(value);
            return TextInputForm.this;
        });
        setTitle("_______Введите текст_______");
        setSizeProvider(values::size);
        setButtonsPerRow(8);
        setMaxRows(6);
        setLimit(getButtonsPerRow()*getMaxRows());
        setValueProvider(i -> {
            if (lowerCase)
                return values.get(i);
            else
                return values.get(i).toUpperCase();
        });
        setTextProvider(() -> {
            String s = sb.toString();
            return s.length() == 0 ? "Введите значение..." : "Ввод:\"<b>"+sb.toString()+"</b>\"";
        });
        Runnable inputFormRender = getPreRender();
        setPreRender(() -> {
            if (inputFormRender != null) inputFormRender.run();
            createGroupBefore(TEXT_INPUT_CONTROLS, CONTROLS_GROUP);
            clearGroup(TEXT_INPUT_CONTROLS);
            RowCreator rowCreator = addRow(TEXT_INPUT_CONTROLS);
            rowCreator.addButton(
                    new FormButton(
                            lowerCase ? "aA" : "Aa",
                            (btn) -> {
                                lowerCase = !lowerCase;
                                return TextInputForm.this;
                            },
                            parent
                    )
            ).addButton(
                    new FormButton(
                            "__",
                            btn -> {
                                sb.append(" ");
                                return TextInputForm.this;
                            },
                            parent
                    )
            );
            if (sb.length() > 0) {
                rowCreator.addButton(
                        new FormButton(
                                "<",
                                btn -> {
                                    sb.deleteCharAt(sb.length()-1);
                                    return TextInputForm.this;
                                },
                                parent
                        )
                ).addButton(
                        new FormButton(
                                "X",
                                btn -> {
                                    sb.delete(0, sb.length());
                                    return TextInputForm.this;
                                },
                                parent
                        )
                );
            }
            rowCreator = rowCreator.and().addRow(TEXT_INPUT_CONTROLS);
            rowCreator.addButton(new FormButton(
                    "ОК",
                    (btn) -> {
                        return inputListener.onInput(this, sb.toString());
                    },
                    this
            ));
        });
    }
}
