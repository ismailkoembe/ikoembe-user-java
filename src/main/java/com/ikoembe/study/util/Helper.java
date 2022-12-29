package com.ikoembe.study.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    public Map<String, Object> classToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();

        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(object);
            map.put(name, value);
        }

        return map;
    }
}
