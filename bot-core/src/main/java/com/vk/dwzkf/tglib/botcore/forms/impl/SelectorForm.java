package com.vk.dwzkf.tglib.botcore.forms.impl;

import com.vk.dwzkf.tglib.botcore.forms.Form;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import com.vk.dwzkf.tglib.botcore.forms.PagingForm;
import com.vk.dwzkf.tglib.botcore.forms.input.SelectorData;
import com.vk.dwzkf.tglib.botcore.forms.listeners.InputListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Roman Shageev
 * @since 16.11.2023
 */
public class SelectorForm<T> extends PagingForm {
    public static final String INPUT_GROUP = "input-group";
    private static final String DEFAULT_TITLE = "Выберите значение:";
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    private int buttonsPerRow = BUTTONS_PER_PAGE_MEDIUM;
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    private int maxRows = 5;
    //TODO: remove fields, use SelectorData class
    @Setter
    @Getter
    private InputListener<T> listener;
    @Setter
    @Getter
    private Function<Integer, T> valueProvider;
    @Getter
    @Setter
    private Supplier<Integer> sizeProvider;
    private Function<T, String> toStringFunction = Objects::toString;

    public static final int BUTTONS_PER_PAGE_TINY = 5;
    public static final int BUTTONS_PER_PAGE_SMALL = 10;
    public static final int BUTTONS_PER_PAGE_MEDIUM = 15;
    public static final int BUTTONS_PER_PAGE_HIGH = 20;

    public class Configurer {
        private int buttonsPerRow = SelectorForm.this.buttonsPerRow;
        public Configurer setButtonsPerRow(int value) {
            if (value < 1) value = 1;
            if (value > BUTTONS_PER_PAGE_HIGH) value = BUTTONS_PER_PAGE_HIGH;
            Configurer.this.buttonsPerRow = value;
            return this;
        }
        public void configure() {
            SelectorForm.this.buttonsPerRow = Configurer.this.buttonsPerRow;
            SelectorForm.this.updateLimit();
        }
    }

    public Configurer config() {
        return new Configurer();
    }

    @Override
    public void onOffsetChanged() {
        int start = getOffset();
        int end = start + getLimit();
        fillPagingButtons(sizeProvider.get());
        addValueButtons(buttonsPerRow, start, end);
    }

    protected SelectorForm(
            Form parent
    ) {
        this(parent, null, null, null, BUTTONS_PER_PAGE_MEDIUM);
    }

    protected SelectorForm(
            Form parent,
            int magicConstant
    ) {
        this(parent, null, null, null, magicConstant);
    }

    public SelectorForm(
            Form parent,
            InputListener<T> inputListener,
            Supplier<List<T>> valuesProvider
    ) {
        this(parent, inputListener, (i) -> valuesProvider.get().get(i), () -> valuesProvider.get().size());
    }

    public SelectorForm(
            Form parent,
            InputListener<T> inputListener,
            Supplier<List<T>> valuesProvider,
            Function<T, String> toStringFunction
    ) {
        this(
                parent, inputListener,
                (i) -> valuesProvider.get().get(i),
                () -> valuesProvider.get().size(),
                toStringFunction
        );
    }

    public SelectorForm(
            Form parent,
            InputListener<T> inputListener,
            Function<Integer, T> valueProvider,
            Supplier<Integer> sizeProvider,
            Function<T, String> toStringFunction
    ) {
        this(parent, inputListener, SelectorData.build(valueProvider, sizeProvider, toStringFunction));
    }

    public SelectorForm(
            Form parent,
            InputListener<T> inputListener,
            SelectorData<T> selectorData
    ) {
        this.toStringFunction = selectorData::render;
        this.listener = inputListener;
        this.valueProvider = selectorData::get;
        this.sizeProvider = selectorData::size;
        updateLimit();
        setTitle(DEFAULT_TITLE);
        createGroup(INPUT_GROUP);
        createGroup(PagingForm.PAGING_GROUP);
        createGroup(PagingForm.CONTROLS_GROUP);

        setParent(parent);
        setPreRender(() -> {
            fillPagingButtons(this.sizeProvider.get());
            addValueButtons(
                    buttonsPerRow,
                    getOffset(), getLimit() + getOffset()
            );
        });
        createControls(parent != null, false);
    }

    public SelectorForm(
            Form parent,
            InputListener<T> inputListener,
            Function<Integer, T> valueProvider,
            Supplier<Integer> sizeProvider
    ) {
        this(parent, inputListener, valueProvider, sizeProvider, Objects::toString);
    }
    /**
     * @param parent        parent form
     * @param inputListener listener on input chosen
     * @param magicConstant % 5 == 0; max = 20 <br/>see:<br/>
     *                        {@link #BUTTONS_PER_PAGE_TINY},<br/>
     *                        {@link #BUTTONS_PER_PAGE_SMALL},<br/>
     *                        {@link #BUTTONS_PER_PAGE_MEDIUM},<br/>
     *                        {@link #BUTTONS_PER_PAGE_HIGH},<br/>
     */
    public SelectorForm(
            Form parent,
            InputListener<T> inputListener,
            Function<Integer, T> valueProvider,
            Supplier<Integer> sizeProvider,
            int magicConstant
    ) {
        this(parent, inputListener, valueProvider, sizeProvider);
        magicConstant = Math.max(magicConstant, 1);
        magicConstant = Math.min(magicConstant, 20);
        this.buttonsPerRow = (int) (Math.ceil(magicConstant / (double) maxRows));
        updateLimit();
    }

    protected void updateLimit() {
        setLimit(buttonsPerRow * maxRows);
    }

    private void addValueButtons(int buttonsPerRow,
                                 int start, int end
    ) {
        clearGroup(INPUT_GROUP);
        int btnCount = 0;
        Form.RowCreator rowCreator = addRow(INPUT_GROUP);
        for (int i = start; i < end && i < sizeProvider.get(); i++) {
            if (btnCount == buttonsPerRow) {
                rowCreator = addRow(INPUT_GROUP);
                btnCount = 0;
            }
            T value = valueProvider.apply(i);
            rowCreator.addButton(
                    new FormButton(
                            toStringFunction.apply(value),
                            btn -> {
                                Form outputForm = listener.onInput(this, value);
                                return outputForm == null ? getParent() : outputForm;
                            },
                            this
                    )
            );
            btnCount++;
        }
    }
}
