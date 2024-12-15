package com.vk.dwzkf.tglib.commons.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public final class EnumHelper {
    private EnumHelper() {
    }

    public interface NumberId<T extends Number> {
        T getId();
    }

    public interface LongId extends NumberId<Long> {
        Long getId();
    }

    public interface IntId extends NumberId<Integer> {
        Integer getId();
    }

    public interface Name {
        String getName();
    }

    public static <R extends Number, T extends Enum<T> & NumberId<R>> boolean is(T enumValue, R id) {
        return enumValue.getId().equals(id);
    }


    public static <R extends Number, T extends Enum<T> & NumberId<R>> T getById(Class<T> enumClass, R id) {
        return get(v -> v.getId().equals(id),
                enumClass.getEnumConstants(),
                String.format("Not found instance of %s with id = %s", enumClass.getSimpleName(), id)
        );
    }

    public static <T extends Enum<T> & Name> T getByName(Class<T> enumClass, String name) {
        return get(v -> v.getName().equals(name),
                enumClass.getEnumConstants(),
                String.format("Not found instance of %s with name = %s", enumClass.getSimpleName(), name)
        );
    }

    public static <R extends Number, T extends Enum<T> & NumberId<R>> T findById(Class<T> enumClass, R id) {
        return find(v -> v.getId().equals(id),
                enumClass.getEnumConstants()
        );
    }

    public static <T extends Enum<T> & Name> T findByName(Class<T> enumClass, String name) {
        return find(v -> v.getName().equals(name),
                enumClass.getEnumConstants()
        );
    }

    public static <T> T get(Predicate<T> pred, T[] values, String errorMessage) {
        T value = find(pred, values);
        if (value != null) {
            return value;
        }
        throw new NoSuchElementException(errorMessage);
    }

    public static <R extends Number & Comparable<R>, T extends Enum<T> & NumberId<R>> List<T> getSorted(
            Class<T> enumClass
    ) {
        T[] enumConstants = enumClass.getEnumConstants();
        List<T> result = Arrays.asList(enumConstants);
        result.sort(Comparator.comparing(NumberId::getId));
        return result;
    }

    public static <T> T find(Predicate<T> pred, T[] values) {
        for (T value : values) {
            if (pred.test(value)) return value;
        }
        return null;
    }
}
