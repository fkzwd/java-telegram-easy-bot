package com.vk.dwzkf.tglib.commons.utils;

import java.util.function.BiFunction;

/**
 * @author Roman Shageev
 * @since 02.12.2023
 */
public class ArrayUtil {
    public static <T> boolean intersect(T[] array1, T[] array2) {
        return intersect(array1, array2, Object::equals);
    }

    public static <T> boolean intersect(T[] array1, T[] array2, BiFunction<T,T,Boolean> tester) {
        for (T obj1 : array1) {
            for (T obj2 : array2) {
                if (tester.apply(obj1, obj2)) return true;
            }
        }
        return false;
    }
}
