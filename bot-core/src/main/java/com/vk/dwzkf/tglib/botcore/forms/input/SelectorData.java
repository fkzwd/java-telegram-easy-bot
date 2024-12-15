package com.vk.dwzkf.tglib.botcore.forms.input;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
public interface SelectorData<T> {
    T get(int index);
    int size();
    String render(T object);

    static  <R> SelectorData<R> build(
            Function<Integer, R> valueProvider,
            Supplier<Integer> sizeProvider,
            Function<R, String> toStringFunction
    ) {
        return new SelectorData<>() {
            @Override
            public R get(int index) {
                return valueProvider.apply(index);
            }

            @Override
            public int size() {
                return sizeProvider.get();
            }

            @Override
            public String render(R object) {
                return toStringFunction.apply(object);
            }
        };
    }
}
