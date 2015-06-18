package com.sysnote.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Morningsun(515190653@qq.com) on 15-6-18.
 */
public class EmptyUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0 || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("undefined");
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
}
