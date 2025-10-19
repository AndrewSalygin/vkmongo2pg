package com.andrewsalygin.util;

import java.util.List;
import java.util.Map;

public final class JsonUtils {

    private JsonUtils() {}

    public static Map<String, Object> getSafeMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedMap = (Map<String, Object>) map;
            return typedMap;
        }

        return null;
    }

    public static List<Map<String, Object>> getSafeList(Map<String, Object> source, String key) {
        Object value = source.get(key);

        if (value instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> typedList = (List<Map<String, Object>>) list;
            return typedList;
        }

        return List.of();
    }
}
